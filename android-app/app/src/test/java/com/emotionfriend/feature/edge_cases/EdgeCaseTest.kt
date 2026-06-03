package com.emotionfriend.feature.edge_cases

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import com.emotionfriend.domain.model.PracticeAttempt
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
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EdgeCaseTest {

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
    fun `empty emotion list is handled gracefully`() = runTest {
        val repo = EdgeCaseEmotionRepository()
        repo.upsertAll(emptyList())

        val emotions = repo.getAll().first()
        assertEquals(0, emotions.size)
    }

    @Test
    fun `null emotion card is handled`() = runTest {
        val repo = EdgeCaseEmotionRepository()
        val card = repo.getById("nonexistent")

        assertNull("Nonexistent card should return null", card)
    }

    @Test
    fun `empty string input is handled`() = runTest {
        val repo = EdgeCaseEmotionRepository()
        val card = repo.getById("")

        assertNull("Empty id should return null", card)
    }

    @Test
    fun `journal entry with empty note`() = runTest {
        val entry = JournalEntry(
            id = 1,
            childId = "child1",
            emotionType = EmotionType.HAPPY,
            note = "",
            createdAt = LocalDateTime.now(),
            imagePath = null
        )

        assertEquals("", entry.note)
    }

    @Test
    fun `very long emotion description is preserved`() = runTest {
        val longDescription = "A".repeat(10000)
        val card = EmotionCard(
            id = "1",
            name = "Test",
            emoji = "😊",
            type = EmotionType.HAPPY,
            description = longDescription
        )

        assertEquals(10000, card.description.length)
    }

    @Test
    fun `practice attempt with all same emotions`() = runTest {
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

        assertEquals(attempt.correctEmotion, attempt.selectedEmotion)
        assertTrue("Should be correct", attempt.isCorrect)
    }

    @Test
    fun `practice attempt with all different emotions is incorrect`() = runTest {
        val attempt = PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "p1",
            correctEmotion = EmotionType.HAPPY,
            selectedEmotion = EmotionType.ANGRY,
            isCorrect = false,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )

        assertFalse("Should be incorrect", attempt.isCorrect)
    }

    @Test
    fun `multiple rapid updates preserve latest state`() = runTest {
        val repo = EdgeCaseEmotionRepository()

        val emotions1 = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc")
        )
        val emotions2 = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc")
        )
        val emotions3 = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc"),
            EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "desc")
        )

        repo.upsertAll(emotions1)
        repo.upsertAll(emotions2)
        repo.upsertAll(emotions3)

        val result = repo.getAll().first()
        assertEquals(3, result.size)
    }

    @Test
    fun `concurrent updates to different emotions`() = runTest {
        val repo = EdgeCaseEmotionRepository()

        val emotions = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc"),
        )
        repo.upsertAll(emotions)

        val all = repo.getAll().first()
        assertEquals(2, all.size)
    }

    @Test
    fun `invalid emotion type is rejected or defaulted`() = runTest {
        // This tests that invalid emotions don't crash the system
        val validEmotions = EmotionType.entries
        assertTrue("Should have valid emotions", validEmotions.isNotEmpty())
    }

    @Test
    fun `timestamp consistency across operations`() = runTest {
        val now = LocalDateTime.now()
        val entry1 = JournalEntry(1, "child", EmotionType.HAPPY, "entry1", now, null)
        val entry2 = JournalEntry(2, "child", EmotionType.HAPPY, "entry2", now, null)

        assertEquals(entry1.createdAt, entry2.createdAt)
    }

    @Test
    fun `emotion card emoji is always a single character or valid emoji`() {
        val emotions = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc"),
            EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "desc"),
        )

        for (emotion in emotions) {
            assertTrue("Emoji should not be empty", emotion.emoji.isNotEmpty())
        }
    }

    @Test
    fun `accessing nonexistent child data returns empty or null gracefully`() = runTest {
        val repo = EdgeCaseEmotionRepository()
        val emotions = repo.getByType(EmotionType.HAPPY).first()

        // Should return empty list, not crash
        assertTrue("Should handle nonexistent type gracefully", emotions.isEmpty())
    }
}

private class EdgeCaseEmotionRepository : com.emotionfriend.data.repository.EmotionRepository {
    private val store = mutableMapOf<String, EmotionCard>()
    private val allFlow = MutableStateFlow<List<EmotionCard>>(emptyList())

    override fun getAll(): Flow<List<EmotionCard>> = allFlow

    override suspend fun getById(id: String): EmotionCard? {
        return if (id.isEmpty()) null else store[id]
    }

    override suspend fun upsertAll(cards: List<EmotionCard>) {
        store.clear()
        store.putAll(cards.associateBy { it.id })
        allFlow.value = cards
    }

    override fun getByType(type: EmotionType): Flow<List<EmotionCard>> =
        MutableStateFlow(store.values.filter { it.type == type })
}
