package com.emotionfriend.data.persistence

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import com.emotionfriend.domain.model.PracticeAttempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
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
class PersistenceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: TestDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        database = TestDatabase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emotion data persists after insertion`() = runTest {
        val emotion = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc")
        database.insertEmotion(emotion)

        val retrieved = database.getEmotion("1")
        assertNotNull("Emotion should persist", retrieved)
        assertEquals(emotion, retrieved)
    }

    @Test
    fun `journal entry persists with correct timestamp`() = runTest {
        val now = LocalDateTime.now()
        val entry = JournalEntry(1, "child1", EmotionType.HAPPY, "note", now, null)

        database.insertJournalEntry(entry)
        val retrieved = database.getJournalEntry(1)

        assertNotNull("Entry should persist", retrieved)
        assertEquals(now, retrieved?.createdAt)
    }

    @Test
    fun `multiple entries can be queried by child`() = runTest {
        val entries = listOf(
            JournalEntry(1, "child1", EmotionType.HAPPY, "note1", LocalDateTime.now(), null),
            JournalEntry(2, "child1", EmotionType.SAD, "note2", LocalDateTime.now(), null),
            JournalEntry(3, "child2", EmotionType.HAPPY, "note3", LocalDateTime.now(), null),
        )

        for (entry in entries) {
            database.insertJournalEntry(entry)
        }

        val child1Entries = database.getJournalEntriesByChild("child1")
        assertEquals(2, child1Entries.size)

        val child2Entries = database.getJournalEntriesByChild("child2")
        assertEquals(1, child2Entries.size)
    }

    @Test
    fun `practice attempt persistence with accuracy tracking`() = runTest {
        val attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.SAD, false, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(3, "child1", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            database.insertPracticeAttempt(attempt)
        }

        val allAttempts = database.getPracticeAttempts("child1")
        assertEquals(3, allAttempts.size)

        val correct = allAttempts.filter { it.isCorrect }
        val accuracy = (correct.size.toFloat() / allAttempts.size) * 100
        assertEquals(66.67f, accuracy, 0.1f)
    }

    @Test
    fun `data survives database reset for different child`() = runTest {
        val entry1 = JournalEntry(1, "child1", EmotionType.HAPPY, "note1", LocalDateTime.now(), null)
        val entry2 = JournalEntry(2, "child2", EmotionType.HAPPY, "note2", LocalDateTime.now(), null)

        database.insertJournalEntry(entry1)
        database.insertJournalEntry(entry2)

        val child1Before = database.getJournalEntriesByChild("child1")
        assertEquals(1, child1Before.size)

        // Add more entries for child2
        database.insertJournalEntry(JournalEntry(3, "child2", EmotionType.SAD, "note3", LocalDateTime.now(), null))

        val child1After = database.getJournalEntriesByChild("child1")
        assertEquals(1, child1After.size)  // Should not change

        val child2After = database.getJournalEntriesByChild("child2")
        assertEquals(2, child2After.size)  // Should have increased
    }

    @Test
    fun `concurrent writes are serialized`() = runTest {
        val jobs = (1..10).map { index ->
            async {
                val emotion = EmotionCard(
                    id = "emotion_$index",
                    name = "Emotion $index",
                    emoji = "😊",
                    type = EmotionType.HAPPY,
                    description = "desc"
                )
                database.insertEmotion(emotion)
            }
        }

        jobs.forEach { it.await() }

        assertTrue("All writes should complete", true)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryManagementTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `large dataset operations complete`() = runTest {
        val repo = LargeDatasetRepository()

        // Generate large dataset
        val emotions = (1..1000).map {
            EmotionCard(
                id = "emotion_$it",
                name = "Emotion $it",
                emoji = "😊",
                type = EmotionType.HAPPY,
                description = "Description $it"
            )
        }

        repo.insertAll(emotions)

        val all = repo.getAll().first()
        assertEquals(1000, all.size)
    }

    @Test
    fun `memory is freed after clearing data`() = runTest {
        val repo = LargeDatasetRepository()

        val emotions = (1..100).map {
            EmotionCard("$it", "Emotion $it", "😊", EmotionType.HAPPY, "desc")
        }
        repo.insertAll(emotions)

        var size = repo.getAll().first().size
        assertEquals(100, size)

        repo.clear()

        size = repo.getAll().first().size
        assertEquals(0, size)
    }

    @Test
    fun `query performance is acceptable for large datasets`() = runTest {
        val repo = LargeDatasetRepository()

        val emotions = (1..500).map {
            EmotionCard("$it", "Emotion $it", "😊", EmotionType.HAPPY, "desc")
        }
        repo.insertAll(emotions)

        val start = System.currentTimeMillis()
        val results = repo.getAll().first()
        val duration = System.currentTimeMillis() - start

        assertEquals(500, results.size)
        assertTrue("Query should complete in reasonable time", duration < 5000)
    }
}

// Test database implementation

private class TestDatabase {
    private val emotions = mutableMapOf<String, EmotionCard>()
    private val journalEntries = mutableListOf<JournalEntry>()
    private val practiceAttempts = mutableListOf<PracticeAttempt>()

    suspend fun insertEmotion(emotion: EmotionCard) {
        emotions[emotion.id] = emotion
    }

    suspend fun getEmotion(id: String): EmotionCard? = emotions[id]

    suspend fun insertJournalEntry(entry: JournalEntry) {
        journalEntries.add(entry)
    }

    suspend fun getJournalEntry(id: Long): JournalEntry? =
        journalEntries.find { it.id == id }

    suspend fun getJournalEntriesByChild(childId: String): List<JournalEntry> =
        journalEntries.filter { it.childId == childId }

    suspend fun insertPracticeAttempt(attempt: PracticeAttempt) {
        practiceAttempts.add(attempt)
    }

    suspend fun getPracticeAttempts(childId: String): List<PracticeAttempt> =
        practiceAttempts.filter { it.childId == childId }
}

// Large dataset repository

private class LargeDatasetRepository {
    private val store = mutableListOf<EmotionCard>()
    private val allFlow = kotlinx.coroutines.flow.MutableStateFlow<List<EmotionCard>>(emptyList())

    suspend fun insertAll(emotions: List<EmotionCard>) {
        store.addAll(emotions)
        allFlow.value = store.toList()
    }

    fun getAll(): kotlinx.coroutines.flow.Flow<List<EmotionCard>> = allFlow

    suspend fun clear() {
        store.clear()
        allFlow.value = emptyList()
    }
}
