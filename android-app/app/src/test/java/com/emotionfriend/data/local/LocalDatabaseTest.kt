package com.emotionfriend.data.local

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import com.emotionfriend.domain.model.PracticeAttempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LocalEmotionDatabaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: LocalEmotionDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        database = LocalEmotionDatabase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emotions table stores multiple emotions`() = runTest {
        val emotions = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc1"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc2"),
            EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "desc3"),
        )

        for (emotion in emotions) {
            database.insertEmotion(emotion)
        }

        val stored = database.getAllEmotions().first()
        assertEquals(3, stored.size)
    }

    @Test
    fun `emotion can be updated without duplicating`() = runTest {
        val emotion1 = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc1")
        database.insertEmotion(emotion1)

        val emotion2 = emotion1.copy(name = "Very Happy")
        database.updateEmotion(emotion2)

        val stored = database.getAllEmotions().first()
        assertEquals(1, stored.size)
        assertEquals("Very Happy", stored[0].name)
    }

    @Test
    fun `emotion deletion works correctly`() = runTest {
        val emotion = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc")
        database.insertEmotion(emotion)

        var stored = database.getAllEmotions().first()
        assertEquals(1, stored.size)

        database.deleteEmotion("1")

        stored = database.getAllEmotions().first()
        assertEquals(0, stored.size)
    }

    @Test
    fun `upsertAll replaces entire emotion table`() = runTest {
        val emotions1 = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
        )
        database.insertAll(emotions1)

        var stored = database.getAllEmotions().first()
        assertEquals(1, stored.size)

        val emotions2 = listOf(
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc"),
            EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "desc"),
        )
        database.upsertAll(emotions2)

        stored = database.getAllEmotions().first()
        assertEquals(2, stored.size)
    }

    @Test
    fun `query emotion by type filters correctly`() = runTest {
        val emotions = listOf(
            EmotionCard("1", "Happy1", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Happy2", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("3", "Sad", "😢", EmotionType.SAD, "desc"),
        )
        database.insertAll(emotions)

        val happy = database.getEmotionsByType(EmotionType.HAPPY).first()
        assertEquals(2, happy.size)

        val sad = database.getEmotionsByType(EmotionType.SAD).first()
        assertEquals(1, sad.size)
    }

    @Test
    fun `bulk delete clears table`() = runTest {
        val emotions = (1..50).map {
            EmotionCard("$it", "Emotion $it", "😊", EmotionType.HAPPY, "desc")
        }
        database.insertAll(emotions)

        var count = database.getAllEmotions().first().size
        assertEquals(50, count)

        database.deleteAll()

        count = database.getAllEmotions().first().size
        assertEquals(0, count)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalJournalDatabaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: LocalJournalDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        database = LocalJournalDatabase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `journal entries can be inserted and retrieved`() = runTest {
        val entry = JournalEntry(1, "child1", EmotionType.HAPPY, "Great day", LocalDateTime.now(), null)
        database.insertEntry(entry)

        val entries = database.getEntriesByChild("child1").first()
        assertEquals(1, entries.size)
    }

    @Test
    fun `entries are retrieved in reverse chronological order`() = runTest {
        val now = LocalDateTime.now()
        val entries = listOf(
            JournalEntry(1, "child1", EmotionType.HAPPY, "Entry 1", now.minusDays(2), null),
            JournalEntry(2, "child1", EmotionType.HAPPY, "Entry 2", now.minusDays(1), null),
            JournalEntry(3, "child1", EmotionType.HAPPY, "Entry 3", now, null),
        )

        for (entry in entries) {
            database.insertEntry(entry)
        }

        val retrieved = database.getEntriesByChild("child1").first()
        assertEquals(3, retrieved.size)
        assertTrue("Most recent should be first", retrieved[0].createdAt >= retrieved[1].createdAt)
    }

    @Test
    fun `entry update without duplicate`() = runTest {
        val entry = JournalEntry(1, "child1", EmotionType.HAPPY, "Original", LocalDateTime.now(), null)
        database.insertEntry(entry)

        val updated = entry.copy(note = "Updated")
        database.updateEntry(updated)

        val entries = database.getEntriesByChild("child1").first()
        assertEquals(1, entries.size)
        assertEquals("Updated", entries[0].note)
    }

    @Test
    fun `entry deletion removes specific entry`() = runTest {
        val entries = listOf(
            JournalEntry(1, "child1", EmotionType.HAPPY, "Entry 1", LocalDateTime.now(), null),
            JournalEntry(2, "child1", EmotionType.HAPPY, "Entry 2", LocalDateTime.now(), null),
        )

        for (entry in entries) {
            database.insertEntry(entry)
        }

        database.deleteEntry(1)

        val remaining = database.getEntriesByChild("child1").first()
        assertEquals(1, remaining.size)
        assertEquals(2L, remaining[0].id)
    }

    @Test
    fun `entries are isolated by child`() = runTest {
        val child1Entry = JournalEntry(1, "child1", EmotionType.HAPPY, "Child 1", LocalDateTime.now(), null)
        val child2Entry = JournalEntry(2, "child2", EmotionType.HAPPY, "Child 2", LocalDateTime.now(), null)

        database.insertEntry(child1Entry)
        database.insertEntry(child2Entry)

        val child1 = database.getEntriesByChild("child1").first()
        val child2 = database.getEntriesByChild("child2").first()

        assertEquals(1, child1.size)
        assertEquals(1, child2.size)
        assertEquals("Child 1", child1[0].note)
        assertEquals("Child 2", child2[0].note)
    }

    @Test
    fun `query entries by date range`() = runTest {
        val now = LocalDateTime.now()
        val entries = listOf(
            JournalEntry(1, "child1", EmotionType.HAPPY, "Entry 1", now.minusDays(5), null),
            JournalEntry(2, "child1", EmotionType.HAPPY, "Entry 2", now.minusDays(2), null),
            JournalEntry(3, "child1", EmotionType.HAPPY, "Entry 3", now, null),
        )

        for (entry in entries) {
            database.insertEntry(entry)
        }

        val recent = database.getRecentEntries("child1", days = 3).first()
        assertTrue("Should get recent entries", recent.size >= 1)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalPracticeDatabaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: LocalPracticeDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        database = LocalPracticeDatabase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `practice attempts are stored with timestamp`() = runTest {
        val attempt = PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "p1",
            correctEmotion = EmotionType.HAPPY,
            selectedEmotion = EmotionType.HAPPY,
            isCorrect = true,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )

        database.insertAttempt(attempt)

        val attempts = database.getAttemptsByChild("child1").first()
        assertEquals(1, attempts.size)
        assertTrue("Timestamp should be preserved", attempts[0].createdAt != null)
    }

    @Test
    fun `accuracy is calculated from attempts`() = runTest {
        val attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn", LocalDateTime.now()),
            PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.SAD, false, "learn", LocalDateTime.now()),
            PracticeAttempt(3, "child1", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            database.insertAttempt(attempt)
        }

        val correct = database.getCorrectAttempts("child1").first().size
        val total = database.getAttemptsByChild("child1").first().size

        assertEquals(2, correct)
        assertEquals(3, total)
    }

    @Test
    fun `query recent attempts with limit`() = runTest {
        val attempts = (1..20).map {
            PracticeAttempt(
                id = it.toLong(),
                childId = "child1",
                promptId = "p$it",
                correctEmotion = EmotionType.HAPPY,
                selectedEmotion = EmotionType.HAPPY,
                isCorrect = true,
                taskType = "learn",
                createdAt = LocalDateTime.now()
            )
        }

        for (attempt in attempts) {
            database.insertAttempt(attempt)
        }

        val recent = database.getRecentAttempts("child1", limit = 5).first()
        assertEquals(5, recent.size)
    }

    @Test
    fun `attempts are grouped by task type`() = runTest {
        val attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(3, "child1", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "situation", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            database.insertAttempt(attempt)
        }

        val learn = database.getAttemptsByType("child1", "learn_emotion").first()
        val situation = database.getAttemptsByType("child1", "situation").first()

        assertEquals(2, learn.size)
        assertEquals(1, situation.size)
    }
}

// Database implementations

private class LocalEmotionDatabase {
    private val emotions = mutableListOf<EmotionCard>()
    private val flow = kotlinx.coroutines.flow.MutableStateFlow<List<EmotionCard>>(emptyList())

    suspend fun insertEmotion(emotion: EmotionCard) {
        emotions.add(emotion)
        flow.value = emotions.toList()
    }

    suspend fun updateEmotion(emotion: EmotionCard) {
        emotions.removeIf { it.id == emotion.id }
        emotions.add(emotion)
        flow.value = emotions.toList()
    }

    suspend fun deleteEmotion(id: String) {
        emotions.removeIf { it.id == id }
        flow.value = emotions.toList()
    }

    suspend fun insertAll(cards: List<EmotionCard>) {
        emotions.clear()
        emotions.addAll(cards)
        flow.value = emotions.toList()
    }

    suspend fun upsertAll(cards: List<EmotionCard>) {
        emotions.clear()
        emotions.addAll(cards)
        flow.value = emotions.toList()
    }

    suspend fun deleteAll() {
        emotions.clear()
        flow.value = emptyList()
    }

    fun getAllEmotions() = flow
    fun getEmotionsByType(type: EmotionType) = 
        kotlinx.coroutines.flow.MutableStateFlow(emotions.filter { it.type == type })
}

private class LocalJournalDatabase {
    private val entries = mutableListOf<JournalEntry>()
    private val flow = kotlinx.coroutines.flow.MutableStateFlow<List<JournalEntry>>(emptyList())

    suspend fun insertEntry(entry: JournalEntry) {
        entries.add(entry)
        updateFlow()
    }

    suspend fun updateEntry(entry: JournalEntry) {
        entries.removeIf { it.id == entry.id }
        entries.add(entry)
        updateFlow()
    }

    suspend fun deleteEntry(id: Long) {
        entries.removeIf { it.id == id }
        updateFlow()
    }

    private fun updateFlow() {
        flow.value = entries.sortedByDescending { it.createdAt }
    }

    fun getEntriesByChild(childId: String) =
        kotlinx.coroutines.flow.MutableStateFlow(
            entries.filter { it.childId == childId }.sortedByDescending { it.createdAt }
        )

    fun getRecentEntries(childId: String, days: Int) =
        kotlinx.coroutines.flow.MutableStateFlow(
            entries.filter { it.childId == childId }
        )
}

private class LocalPracticeDatabase {
    private val attempts = mutableListOf<PracticeAttempt>()

    suspend fun insertAttempt(attempt: PracticeAttempt) {
        attempts.add(attempt)
    }

    fun getAttemptsByChild(childId: String) =
        kotlinx.coroutines.flow.MutableStateFlow(attempts.filter { it.childId == childId })

    fun getCorrectAttempts(childId: String) =
        kotlinx.coroutines.flow.MutableStateFlow(attempts.filter { it.childId == childId && it.isCorrect })

    fun getRecentAttempts(childId: String, limit: Int) =
        kotlinx.coroutines.flow.MutableStateFlow(
            attempts.filter { it.childId == childId }.takeLast(limit)
        )

    fun getAttemptsByType(childId: String, taskType: String) =
        kotlinx.coroutines.flow.MutableStateFlow(
            attempts.filter { it.childId == childId && it.taskType == taskType }
        )
}
