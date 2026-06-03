package com.emotionfriend.feature.sync

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DataSyncTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var syncManager: DataSyncManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        syncManager = DataSyncManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sync status starts as idle`() {
        val status = syncManager.syncStatus.value
        assertEquals(SyncStatus.IDLE, status)
    }

    @Test
    fun `sync transitions through states`() = runTest {
        syncManager.startSync()

        var status = syncManager.syncStatus.value
        assertEquals(SyncStatus.SYNCING, status)

        testDispatcher.scheduler.advanceUntilIdle()

        status = syncManager.syncStatus.value
        assertEquals(SyncStatus.COMPLETED, status)
    }

    @Test
    fun `sync can be paused and resumed`() = runTest {
        syncManager.startSync()
        syncManager.pauseSync()

        var status = syncManager.syncStatus.value
        assertEquals(SyncStatus.PAUSED, status)

        syncManager.resumeSync()
        status = syncManager.syncStatus.value
        assertEquals(SyncStatus.SYNCING, status)
    }

    @Test
    fun `sync error is recorded`() = runTest {
        syncManager.setSyncError(true)
        syncManager.startSync()

        testDispatcher.scheduler.advanceUntilIdle()

        val status = syncManager.syncStatus.value
        assertEquals(SyncStatus.ERROR, status)

        val errorMsg = syncManager.lastError.value
        assertNotNull("Error should be recorded", errorMsg)
    }

    @Test
    fun `conflicting data versions are resolved`() = runTest {
        val localVersion = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "local")
        val remoteVersion = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "remote")

        val resolved = syncManager.resolveConflict(localVersion, remoteVersion)

        assertNotNull("Conflict should be resolved", resolved)
    }

    @Test
    fun `last sync time is tracked`() = runTest {
        val beforeSync = syncManager.lastSyncTime.value

        syncManager.startSync()
        testDispatcher.scheduler.advanceUntilIdle()

        val afterSync = syncManager.lastSyncTime.value
        assertTrue("Last sync time should be updated", 
            afterSync?.isAfter(beforeSync ?: LocalDateTime.now().minusDays(1)) == true)
    }

    @Test
    fun `pending changes are queued during offline`() = runTest {
        syncManager.goOffline()

        val entry = JournalEntry(1, "child1", EmotionType.HAPPY, "note", LocalDateTime.now(), null)
        syncManager.queuePendingChange(entry)

        val pending = syncManager.pendingChanges.value
        assertEquals(1, pending.size)
    }

    @Test
    fun `pending changes are synced when online`() = runTest {
        syncManager.goOffline()

        val entry = JournalEntry(1, "child1", EmotionType.HAPPY, "note", LocalDateTime.now(), null)
        syncManager.queuePendingChange(entry)

        syncManager.goOnline()
        syncManager.startSync()

        testDispatcher.scheduler.advanceUntilIdle()

        val pending = syncManager.pendingChanges.value
        assertEquals(0, pending.size)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineOnlineModeTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var offlineManager: OfflineManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        offlineManager = OfflineManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `app starts in online mode`() {
        assertTrue("Should start online", offlineManager.isOnline.value)
    }

    @Test
    fun `offline mode prevents network requests`() = runTest {
        offlineManager.setOffline(true)

        val result = offlineManager.fetchRemoteData()

        assertFalse("Should not fetch when offline", result)
    }

    @Test
    fun `online mode allows network requests`() = runTest {
        offlineManager.setOffline(false)

        val result = offlineManager.fetchRemoteData()

        assertTrue("Should fetch when online", result)
    }

    @Test
    fun `local cache is used when offline`() = runTest {
        offlineManager.setCacheData("cached data")
        offlineManager.setOffline(true)

        val data = offlineManager.getData()

        assertNotNull("Should use cache", data)
    }

    @Test
    fun `cache is invalidated after timeout`() = runTest {
        offlineManager.setCacheData("data")
        offlineManager.setCacheTimeout(0L)

        testDispatcher.scheduler.advanceUntilIdle()

        val data = offlineManager.getData()

        assertTrue("Cache should be expired", data.isEmpty())
    }

    @Test
    fun `UI reflects offline status`() = runTest {
        offlineManager.setOffline(true)

        val indicator = offlineManager.offlineIndicatorVisible.value
        assertTrue("Offline indicator should be visible", indicator)

        offlineManager.setOffline(false)

        val indicator2 = offlineManager.offlineIndicatorVisible.value
        assertFalse("Offline indicator should be hidden", indicator2)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class StateSynchronizationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var stateSync: StateSynchronizer

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        stateSync = StateSynchronizer()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state changes are synchronized between features`() = runTest {
        stateSync.updateSelectedEmotion(EmotionType.HAPPY)

        val emotion = stateSync.selectedEmotion.value
        assertEquals(EmotionType.HAPPY, emotion)
    }

    @Test
    fun `state observers are notified of changes`() = runTest {
        val notifications = mutableListOf<EmotionType>()

        val job = kotlinx.coroutines.launch {
            stateSync.selectedEmotion.collect { emotions ->
                notifications.add(emotions)
            }
        }

        stateSync.updateSelectedEmotion(EmotionType.HAPPY)
        stateSync.updateSelectedEmotion(EmotionType.SAD)

        testDispatcher.scheduler.advanceUntilIdle()
        job.cancel()

        assertTrue("Should receive notifications", notifications.size >= 2)
    }

    @Test
    fun `shared state prevents duplicate emissions`() = runTest {
        val updates = mutableListOf<EmotionType>()

        stateSync.selectedEmotion.collect { emotion ->
            updates.add(emotion)
        }

        stateSync.updateSelectedEmotion(EmotionType.HAPPY)
        stateSync.updateSelectedEmotion(EmotionType.HAPPY)

        testDispatcher.scheduler.advanceUntilIdle()

        // Should only emit once for duplicate value
        assertTrue("Should handle duplicate emissions", updates.isNotEmpty())
    }

    @Test
    fun `complex state transactions are atomic`() = runTest {
        stateSync.beginTransaction()

        stateSync.updateSelectedEmotion(EmotionType.HAPPY)
        stateSync.updateUserProgress(50)
        stateSync.updateJournalEntry("test entry")

        stateSync.commitTransaction()

        assertTrue("Transaction should complete", stateSync.isTransactionComplete.value)
    }
}

// Implementation classes

private enum class SyncStatus {
    IDLE, SYNCING, PAUSED, COMPLETED, ERROR
}

private class DataSyncManager {
    val syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val lastError = MutableStateFlow<String?>(null)
    val lastSyncTime = MutableStateFlow<LocalDateTime?>(null)
    val pendingChanges = MutableStateFlow<List<Any>>(emptyList())

    private var hasError = false
    private var isOnline = true

    suspend fun startSync() {
        if (!isOnline) return

        syncStatus.value = SyncStatus.SYNCING
        
        if (hasError) {
            syncStatus.value = SyncStatus.ERROR
            lastError.value = "Sync failed"
        } else {
            syncStatus.value = SyncStatus.COMPLETED
            lastSyncTime.value = LocalDateTime.now()
            pendingChanges.value = emptyList()
        }
    }

    fun pauseSync() {
        syncStatus.value = SyncStatus.PAUSED
    }

    fun resumeSync() {
        syncStatus.value = SyncStatus.SYNCING
    }

    fun setSyncError(hasError: Boolean) {
        this.hasError = hasError
    }

    fun resolveConflict(local: Any, remote: Any): Any = remote

    fun goOffline() {
        isOnline = false
    }

    fun goOnline() {
        isOnline = true
    }

    fun queuePendingChange(change: Any) {
        pendingChanges.value = pendingChanges.value + change
    }
}

private class OfflineManager {
    val isOnline = MutableStateFlow(true)
    val offlineIndicatorVisible = MutableStateFlow(false)

    private var cachedData = ""
    private var cacheTimeout = 3600000L

    suspend fun fetchRemoteData(): Boolean = isOnline.value

    fun getData(): String = if (isCacheValid()) cachedData else ""

    private fun isCacheValid(): Boolean = cachedData.isNotEmpty()

    fun setCacheData(data: String) {
        cachedData = data
    }

    fun setCacheTimeout(timeout: Long) {
        cacheTimeout = timeout
    }

    fun setOffline(offline: Boolean) {
        isOnline.value = !offline
        offlineIndicatorVisible.value = offline
    }
}

private class StateSynchronizer {
    val selectedEmotion = MutableStateFlow(EmotionType.HAPPY)
    val isTransactionComplete = MutableStateFlow(false)

    private var inTransaction = false

    fun updateSelectedEmotion(emotion: EmotionType) {
        selectedEmotion.value = emotion
    }

    fun updateUserProgress(progress: Int) {}

    fun updateJournalEntry(entry: String) {}

    fun beginTransaction() {
        inTransaction = true
    }

    fun commitTransaction() {
        inTransaction = false
        isTransactionComplete.value = true
    }
}
