package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoriesTest {

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
    fun `emotion repository can store and retrieve emotions`() = runTest {
        val repo = InMemoryEmotionRepository()
        val emotion = EmotionCard(
            id = "1",
            name = "Happy",
            emoji = "😊",
            type = EmotionType.HAPPY,
            description = "Happy feeling"
        )

        repo.upsertAll(listOf(emotion))
        val retrieved = repo.getById("1")

        assertEquals("1", retrieved?.id)
        assertEquals("Happy", retrieved?.name)
    }

    @Test
    fun `emotion repository getByType filters correctly`() = runTest {
        val repo = InMemoryEmotionRepository()
        val emotions = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "Happy"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "Sad"),
            EmotionCard("3", "Happy2", "😊", EmotionType.HAPPY, "Happy2"),
        )

        repo.upsertAll(emotions)
        val happy = repo.getByType(EmotionType.HAPPY).first()

        assertEquals(2, happy.size)
    }

    @Test
    fun `journal repository stores and retrieves entries`() = runTest {
        val repo = InMemoryJournalRepository()
        val entry = JournalEntry(
            id = 1,
            childId = "child1",
            emotionType = EmotionType.HAPPY,
            note = "Great day",
            createdAt = LocalDateTime.now(),
            imagePath = null
        )

        repo.insert(entry)
        val retrieved = repo.getByChildId("child1").first()

        assertEquals(1, retrieved.size)
        assertEquals("Great day", retrieved[0].note)
    }

    @Test
    fun `journal repository retrieves all entries for child`() = runTest {
        val repo = InMemoryJournalRepository()
        val entries = listOf(
            JournalEntry(1, "child1", EmotionType.HAPPY, "Note 1", LocalDateTime.now(), null),
            JournalEntry(2, "child1", EmotionType.SAD, "Note 2", LocalDateTime.now(), null),
            JournalEntry(3, "child2", EmotionType.CALM, "Note 3", LocalDateTime.now(), null),
        )

        for (entry in entries) {
            repo.insert(entry)
        }

        val child1Entries = repo.getByChildId("child1").first()
        assertEquals(2, child1Entries.size)

        val child2Entries = repo.getByChildId("child2").first()
        assertEquals(1, child2Entries.size)
    }

    @Test
    fun `scenario repository stores and retrieves scenarios`() = runTest {
        val repo = InMemoryScenarioRepository()
        val scenario = com.emotionfriend.domain.model.ScenarioLesson(
            id = "1",
            title = "Scenario 1",
            situationText = "Test situation",
            imageName = null,
            correctEmotion = EmotionType.HAPPY,
            options = listOf(EmotionType.HAPPY, EmotionType.SAD),
            explanation = "Explanation"
        )

        repo.upsertAll(listOf(scenario))
        val retrieved = repo.getById("1")

        assertNotNull("Scenario should be retrieved", retrieved)
        assertEquals("Scenario 1", retrieved?.title)
    }

    @Test
    fun `story repository stores and retrieves stories`() = runTest {
        val repo = InMemoryStoryRepository()
        val story = com.emotionfriend.domain.model.Story(
            id = "1",
            title = "Story 1",
            content = "Once upon a time...",
            imageName = null,
            order = 1
        )

        repo.upsertAll(listOf(story))
        val retrieved = repo.getById("1")

        assertNotNull("Story should be retrieved", retrieved)
        assertEquals("Story 1", retrieved?.title)
    }

    @Test
    fun `practice repository stores and retrieves attempts`() = runTest {
        val repo = InMemoryPracticeRepository()
        val attempt = com.emotionfriend.domain.model.PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "prompt1",
            correctEmotion = EmotionType.HAPPY,
            selectedEmotion = EmotionType.HAPPY,
            isCorrect = true,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )

        repo.insert(attempt)
        val attempts = repo.getByChildId("child1").first()

        assertEquals(1, attempts.size)
        assertEquals(true, attempts[0].isCorrect)
    }

    @Test
    fun `practice repository filters recent attempts correctly`() = runTest {
        val repo = InMemoryPracticeRepository()
        val attempts = listOf(
            com.emotionfriend.domain.model.PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now().minusDays(1)),
            com.emotionfriend.domain.model.PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            com.emotionfriend.domain.model.PracticeAttempt(3, "child1", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            repo.insert(attempt)
        }

        val recent = repo.getRecentByChildId("child1", 2).first()
        assertEquals(2, recent.size)
    }
}

// In-memory implementations for testing

private class InMemoryEmotionRepository : EmotionRepository {
    private val store = mutableMapOf<String, EmotionCard>()
    private val allFlow = kotlinx.coroutines.flow.MutableStateFlow<List<EmotionCard>>(emptyList())

    override fun getAll(): kotlinx.coroutines.flow.Flow<List<EmotionCard>> = allFlow

    override suspend fun getById(id: String): EmotionCard? = store[id]

    override suspend fun upsertAll(cards: List<EmotionCard>> {
        store.clear()
        for (card in cards) {
            store[card.id] = card
        }
        allFlow.value = cards
    }

    override fun getByType(type: EmotionType): kotlinx.coroutines.flow.Flow<List<EmotionCard>> {
        return kotlinx.coroutines.flow.MutableStateFlow(allFlow.value.filter { it.type == type })
    }
}

private class InMemoryJournalRepository : JournalRepository {
    private val store = mutableListOf<JournalEntry>()

    override fun getByChildId(childId: String): kotlinx.coroutines.flow.Flow<List<JournalEntry>> {
        return kotlinx.coroutines.flow.MutableStateFlow(store.filter { it.childId == childId })
    }

    override fun getAllByChildId(childId: String): kotlinx.coroutines.flow.Flow<List<JournalEntry>> {
        return kotlinx.coroutines.flow.MutableStateFlow(store.filter { it.childId == childId })
    }

    override suspend fun insert(entry: JournalEntry) {
        store.add(entry)
    }
}

private class InMemoryScenarioRepository : ScenarioRepository {
    private val store = mutableMapOf<String, com.emotionfriend.domain.model.ScenarioLesson>()
    private val allFlow = kotlinx.coroutines.flow.MutableStateFlow<List<com.emotionfriend.domain.model.ScenarioLesson>>(emptyList())

    override fun getAll(): kotlinx.coroutines.flow.Flow<List<com.emotionfriend.domain.model.ScenarioLesson>> = allFlow

    override suspend fun getById(id: String): com.emotionfriend.domain.model.ScenarioLesson? = store[id]

    override suspend fun upsertAll(lessons: List<com.emotionfriend.domain.model.ScenarioLesson>) {
        store.clear()
        for (lesson in lessons) {
            store[lesson.id] = lesson
        }
        allFlow.value = lessons
    }
}

private class InMemoryStoryRepository : StoryRepository {
    private val store = mutableMapOf<String, com.emotionfriend.domain.model.Story>()
    private val allFlow = kotlinx.coroutines.flow.MutableStateFlow<List<com.emotionfriend.domain.model.Story>>(emptyList())

    override fun getAll(): kotlinx.coroutines.flow.Flow<List<com.emotionfriend.domain.model.Story>> = allFlow

    override suspend fun getById(id: String): com.emotionfriend.domain.model.Story? = store[id]

    override suspend fun upsertAll(stories: List<com.emotionfriend.domain.model.Story>) {
        store.clear()
        for (story in stories) {
            store[story.id] = story
        }
        allFlow.value = stories
    }
}

private class InMemoryPracticeRepository : PracticeRepository {
    private val store = mutableListOf<com.emotionfriend.domain.model.PracticeAttempt>()

    override fun getByChildId(childId: String): kotlinx.coroutines.flow.Flow<List<com.emotionfriend.domain.model.PracticeAttempt>> {
        return kotlinx.coroutines.flow.MutableStateFlow(store.filter { it.childId == childId })
    }

    override fun getRecentByChildId(childId: String, limit: Int): kotlinx.coroutines.flow.Flow<List<com.emotionfriend.domain.model.PracticeAttempt>> {
        return kotlinx.coroutines.flow.MutableStateFlow(store.filter { it.childId == childId }.takeLast(limit))
    }

    override suspend fun insert(attempt: com.emotionfriend.domain.model.PracticeAttempt) {
        store.add(attempt)
    }
}
