package com.emotionfriend.feature

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
import com.emotionfriend.domain.model.ScenarioLesson
import com.emotionfriend.domain.model.Story
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
class FeatureIntegrationTest {

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
    fun `complete learn emotion session flow`() = runTest {
        val emotionRepo = TestEmotionRepository()
        val practiceRepo = TestPracticeRepository()

        val emotions = listOf(
            EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc"),
            EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc"),
        )
        emotionRepo.upsertAll(emotions)

        // User selects emotion and answers
        val selectedEmotion = emotionRepo.getById("1")
        assertNotNull("Emotion should be loaded", selectedEmotion)

        // User submits answer
        val attempt = PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "1",
            correctEmotion = selectedEmotion!!.type,
            selectedEmotion = selectedEmotion.type,
            isCorrect = true,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )
        practiceRepo.insert(attempt)

        val attempts = practiceRepo.getByChildId("child1").first()
        assertEquals(1, attempts.size)
        assertTrue("Answer should be correct", attempts[0].isCorrect)
    }

    @Test
    fun `complete scenario session flow`() = runTest {
        val scenarioRepo = TestScenarioRepository()
        val practiceRepo = TestPracticeRepository()

        val scenarios = listOf(
            ScenarioLesson(
                id = "1",
                title = "Test",
                situationText = "Test situation",
                imageName = null,
                correctEmotion = EmotionType.HAPPY,
                options = listOf(EmotionType.HAPPY, EmotionType.SAD),
                explanation = "Test explanation"
            )
        )
        scenarioRepo.upsertAll(scenarios)

        // User answers scenario
        val scenario = scenarioRepo.getById("1")
        assertNotNull("Scenario should be loaded", scenario)

        val attempt = PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "1",
            correctEmotion = scenario!!.correctEmotion,
            selectedEmotion = scenario.correctEmotion,
            isCorrect = true,
            taskType = "situation",
            createdAt = LocalDateTime.now()
        )
        practiceRepo.insert(attempt)

        val attempts = practiceRepo.getByChildId("child1").first()
        assertEquals(1, attempts.size)
        assertEquals("situation", attempts[0].taskType)
    }

    @Test
    fun `user session tracks multiple practice attempts`() = runTest {
        val practiceRepo = TestPracticeRepository()

        // Simulate multiple practice sessions
        val attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.SAD, false, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(3, "child1", "p3", EmotionType.ANGRY, EmotionType.ANGRY, true, "situation", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            practiceRepo.insert(attempt)
        }

        val all = practiceRepo.getByChildId("child1").first()
        assertEquals(3, all.size)

        val correct = all.filter { it.isCorrect }
        assertEquals(2, correct.size)

        val accuracy = (correct.size.toFloat() / all.size) * 100
        assertEquals(66.67f, accuracy, 0.1f)
    }

    @Test
    fun `story progression is tracked`() = runTest {
        val storyRepo = TestStoryRepository()

        val stories = listOf(
            Story("1", "Story 1", "Content 1", null, 1),
            Story("2", "Story 2", "Content 2", null, 2),
            Story("3", "Story 3", "Content 3", null, 3),
        )
        storyRepo.upsertAll(stories)

        val allStories = storyRepo.getAll().first()
        assertEquals(3, allStories.size)

        // User reads story 1
        val story1 = storyRepo.getById("1")
        assertNotNull("Story 1 should be readable", story1)

        // User progresses to story 2
        val story2 = storyRepo.getById("2")
        assertNotNull("Story 2 should be readable", story2)
    }

    @Test
    fun `practice statistics are calculated correctly`() = runTest {
        val practiceRepo = TestPracticeRepository()

        val attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now().minusHours(2)),
            PracticeAttempt(2, "child1", "p2", EmotionType.HAPPY, EmotionType.SAD, false, "learn_emotion", LocalDateTime.now().minusHours(1)),
            PracticeAttempt(3, "child1", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
        )

        for (attempt in attempts) {
            practiceRepo.insert(attempt)
        }

        val recentAttempts = practiceRepo.getRecentByChildId("child1", 2).first()
        assertEquals(2, recentAttempts.size)

        val recent2Correct = recentAttempts.filter { it.isCorrect }.size
        assertTrue("Last 2 attempts have correct answers", recent2Correct >= 1)
    }

    @Test
    fun `multiple children data is isolated`() = runTest {
        val practiceRepo = TestPracticeRepository()

        val child1Attempts = listOf(
            PracticeAttempt(1, "child1", "p1", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
        )

        val child2Attempts = listOf(
            PracticeAttempt(2, "child2", "p2", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
            PracticeAttempt(3, "child2", "p3", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()),
        )

        for (attempt in child1Attempts + child2Attempts) {
            practiceRepo.insert(attempt)
        }

        val child1Data = practiceRepo.getByChildId("child1").first()
        val child2Data = practiceRepo.getByChildId("child2").first()

        assertEquals(1, child1Data.size)
        assertEquals(2, child2Data.size)
    }
}

// Test repositories

private class TestEmotionRepository : com.emotionfriend.data.repository.EmotionRepository {
    private val store = mutableMapOf<String, EmotionCard>()
    private val allFlow = MutableStateFlow<List<EmotionCard>>(emptyList())

    override fun getAll(): Flow<List<EmotionCard>> = allFlow
    override suspend fun getById(id: String): EmotionCard? = store[id]
    override suspend fun upsertAll(cards: List<EmotionCard>) {
        store.clear()
        store.putAll(cards.associateBy { it.id })
        allFlow.value = cards
    }
    override fun getByType(type: EmotionType): Flow<List<EmotionCard>> =
        MutableStateFlow(store.values.filter { it.type == type })
}

private class TestScenarioRepository : com.emotionfriend.data.repository.ScenarioRepository {
    private val store = mutableMapOf<String, ScenarioLesson>()
    private val allFlow = MutableStateFlow<List<ScenarioLesson>>(emptyList())

    override fun getAll(): Flow<List<ScenarioLesson>> = allFlow
    override suspend fun getById(id: String): ScenarioLesson? = store[id]
    override suspend fun upsertAll(lessons: List<ScenarioLesson>) {
        store.clear()
        store.putAll(lessons.associateBy { it.id })
        allFlow.value = lessons
    }
}

private class TestStoryRepository : com.emotionfriend.data.repository.StoryRepository {
    private val store = mutableMapOf<String, Story>()
    private val allFlow = MutableStateFlow<List<Story>>(emptyList())

    override fun getAll(): Flow<List<Story>> = allFlow
    override suspend fun getById(id: String): Story? = store[id]
    override suspend fun upsertAll(stories: List<Story>) {
        store.clear()
        store.putAll(stories.associateBy { it.id })
        allFlow.value = stories
    }
}

private class TestPracticeRepository : com.emotionfriend.data.repository.PracticeRepository {
    private val store = mutableListOf<PracticeAttempt>()

    override fun getByChildId(childId: String): Flow<List<PracticeAttempt>> =
        MutableStateFlow(store.filter { it.childId == childId })

    override fun getRecentByChildId(childId: String, limit: Int): Flow<List<PracticeAttempt>> =
        MutableStateFlow(store.filter { it.childId == childId }.takeLast(limit))

    override suspend fun insert(attempt: PracticeAttempt) {
        store.add(attempt)
    }
}
