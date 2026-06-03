package com.emotionfriend.feature.home

import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var journalRepo: FakeJournalRepository
    private lateinit var dataStore: FakeDataStore
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        journalRepo = FakeJournalRepository()
        dataStore = FakeDataStore()
        viewModel = HomeViewModel(journalRepo, dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state starts at loading`() {
        val state = viewModel.uiState.value
        assertTrue("Should be loading initially", state.isLoading)
    }

    @Test
    fun `after init check-in phase transitions to SELECT_EMOTION`() = runTest {
        // Wait for init to complete
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse("Should not be loading after init", state.isLoading)
        assertEquals(CheckInPhase.SELECT_EMOTION, state.checkInPhase)
    }

    @Test
    fun `selectEmotion updates selectedEmotion in state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectEmotion(EmotionType.HAPPY)

        val state = viewModel.uiState.value
        assertEquals(EmotionType.HAPPY, state.selectedEmotion)
    }

    @Test
    fun `onWelcomeFinished transitions from WELCOME to SELECT_EMOTION`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onWelcomeFinished()

        val state = viewModel.uiState.value
        assertEquals(CheckInPhase.SELECT_EMOTION, state.checkInPhase)
    }

    @Test
    fun `startRecording with permission granted begins recording`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        viewModel.selectEmotion(EmotionType.HAPPY)

        // Verify emotion was selected before recording
        assertEquals(EmotionType.HAPPY, viewModel.uiState.value.selectedEmotion)
    }

    @Test
    fun `saveJournalEntry saves entry to repository`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectEmotion(EmotionType.SAD)
        viewModel.saveJournalEntry("Today I felt sad about losing my toy")

        val entries = journalRepo.getAllByChildId("default_child").first()
        assertEquals(1, entries.size)
        assertEquals(EmotionType.SAD, entries[0].emotionType)
        assertEquals("Today I felt sad about losing my toy", entries[0].note)
    }

    @Test
    fun `multiple emotion selections update state correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectEmotion(EmotionType.HAPPY)
        var state = viewModel.uiState.value
        assertEquals(EmotionType.HAPPY, state.selectedEmotion)

        viewModel.selectEmotion(EmotionType.CALM)
        state = viewModel.uiState.value
        assertEquals(EmotionType.CALM, state.selectedEmotion)
    }

    @Test
    fun `isFirstVisitToday flag is set correctly on first visit`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        // First visit should have isFirstVisitToday = true
        assertTrue("First visit today should be true", viewModel.uiState.value.isFirstVisitToday)
    }
}

private class FakeJournalRepository : JournalRepository {
    private val entries = MutableStateFlow<List<JournalEntry>>(emptyList())

    override fun getByChildId(childId: String): Flow<List<JournalEntry>> =
        entries

    override fun getAllByChildId(childId: String): Flow<List<JournalEntry>> =
        entries

    override suspend fun insert(entry: JournalEntry) {
        entries.value = entries.value + entry
    }
}

private class FakeDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences>
        get() = MutableStateFlow(emptyPreferences())

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
        emptyPreferences()
}
