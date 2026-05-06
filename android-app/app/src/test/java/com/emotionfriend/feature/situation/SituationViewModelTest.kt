package com.emotionfriend.feature.situation

import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.data.repository.ScenarioRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
import com.emotionfriend.domain.model.ScenarioLesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class SituationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testScenarios = listOf(
        ScenarioLesson(
            id             = "s1",
            title          = "Tình huống 1",
            situationText  = "Bạn nhận được món quà bất ngờ.",
            imageName      = null,
            correctEmotion = EmotionType.HAPPY,
            options        = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.ANGRY, EmotionType.SURPRISED),
            explanation    = "Nhận quà thường mang lại cảm giác vui vẻ."
        ),
        ScenarioLesson(
            id             = "s2",
            title          = "Tình huống 2",
            situationText  = "Bạn thân của bạn chuyển trường.",
            imageName      = null,
            correctEmotion = EmotionType.SAD,
            options        = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.CALM, EmotionType.TIRED),
            explanation    = "Xa bạn bè thường khiến chúng ta cảm thấy buồn."
        ),
        ScenarioLesson(
            id             = "s3",
            title          = "Tình huống 3",
            situationText  = "Bạn bị mất đồ chơi yêu thích.",
            imageName      = null,
            correctEmotion = EmotionType.ANGRY,
            options        = listOf(EmotionType.ANGRY, EmotionType.CALM, EmotionType.SURPRISED, EmotionType.TIRED),
            explanation    = "Mất đồ vật quý giá có thể khiến bạn tức giận."
        )
    )

    private lateinit var scenarioRepo: FakeScenarioRepository
    private lateinit var practiceRepo: FakePracticeRepository
    private lateinit var viewModel: SituationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scenarioRepo = FakeScenarioRepository(testScenarios)
        practiceRepo = FakePracticeRepository()
        viewModel = SituationViewModel(scenarioRepo, practiceRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -----------------------------------------------------------------------
    // Test case 1 — Loads first scenario
    // -----------------------------------------------------------------------

    @Test
    fun `initial state loads first scenario`() = runTest {
        val state = viewModel.uiState.value

        assertFalse("Should not still be loading", state.isLoading)
        assertNotNull("currentScenario should be set", state.currentScenario)
        assertEquals("questionIndex should start at 0", 0, state.questionIndex)
        assertEquals("totalQuestions should equal number of scenarios", testScenarios.size, state.totalQuestions)
        assertFalse("Session should not be complete at start", state.isSessionComplete)
        assertNull("No answer selected yet", state.selectedEmotion)
        assertFalse("Answer should not be submitted yet", state.isAnswerSubmitted)
    }

    // -----------------------------------------------------------------------
    // Test case 2 — Selecting correct emotion shows correct feedback
    // -----------------------------------------------------------------------

    @Test
    fun `selecting correct emotion and submitting shows correct feedback`() = runTest {
        val correctEmotion = viewModel.uiState.value.currentScenario!!.correctEmotion

        viewModel.selectEmotion(correctEmotion)
        viewModel.submitAnswer()

        val state = viewModel.uiState.value
        assertTrue("Answer should be marked submitted", state.isAnswerSubmitted)
        assertEquals("isCorrect should be true", true, state.isCorrect)
        assertTrue("Explanation should be populated", state.explanation.isNotEmpty())
    }

    // -----------------------------------------------------------------------
    // Test case 3 — Selecting wrong emotion shows explanation
    // -----------------------------------------------------------------------

    @Test
    fun `selecting wrong emotion and submitting shows explanation`() = runTest {
        val scenario = viewModel.uiState.value.currentScenario!!
        val wrongEmotion = EmotionType.entries.first { it != scenario.correctEmotion }

        viewModel.selectEmotion(wrongEmotion)
        viewModel.submitAnswer()

        val state = viewModel.uiState.value
        assertTrue("Answer should be marked submitted", state.isAnswerSubmitted)
        assertEquals("isCorrect should be false", false, state.isCorrect)
        assertEquals(
            "Explanation text should match scenario explanation",
            scenario.explanation,
            state.explanation
        )
    }

    // -----------------------------------------------------------------------
    // Test case 4 — PracticeAttempt is saved after submit
    // -----------------------------------------------------------------------

    @Test
    fun `submitting answer saves a PracticeAttempt to repository`() = runTest {
        val scenario = viewModel.uiState.value.currentScenario!!

        viewModel.selectEmotion(scenario.correctEmotion)
        viewModel.submitAnswer()

        assertEquals("Exactly one attempt should be saved", 1, practiceRepo.insertedAttempts.size)

        val attempt = practiceRepo.insertedAttempts.first()
        assertEquals("promptId should match scenario id", scenario.id, attempt.promptId)
        assertEquals("correctEmotion should match scenario correctEmotion", scenario.correctEmotion, attempt.correctEmotion)
        assertEquals("selectedEmotion should match what was selected", scenario.correctEmotion, attempt.selectedEmotion)
        assertEquals("isCorrect should be true for correct answer", true, attempt.isCorrect)
        assertEquals("taskType should be situation", "situation", attempt.taskType)
    }

    // -----------------------------------------------------------------------
    // Test case 5 — Next scenario works
    // -----------------------------------------------------------------------

    @Test
    fun `calling nextScenario advances to the next scenario`() = runTest {
        val firstScenario = viewModel.uiState.value.currentScenario!!

        viewModel.selectEmotion(firstScenario.correctEmotion)
        viewModel.submitAnswer()
        viewModel.nextScenario()

        val state = viewModel.uiState.value
        assertEquals("questionIndex should advance to 1", 1, state.questionIndex)
        assertNotNull("New scenario should be set", state.currentScenario)
        assertFalse("isAnswerSubmitted should be reset", state.isAnswerSubmitted)
        assertNull("selectedEmotion should be cleared", state.selectedEmotion)
        assertNull("isCorrect should be reset", state.isCorrect)
        assertTrue("explanation should be cleared", state.explanation.isEmpty())
        assertFalse("Session should not be complete yet", state.isSessionComplete)
    }
}

// ---------------------------------------------------------------------------
// Fake repositories
// ---------------------------------------------------------------------------

private class FakeScenarioRepository(
    scenarios: List<ScenarioLesson>
) : ScenarioRepository {
    private val flow = MutableStateFlow(scenarios)

    override fun getAll(): Flow<List<ScenarioLesson>> = flow
    override suspend fun getById(id: String): ScenarioLesson? = flow.value.find { it.id == id }
    override suspend fun upsertAll(lessons: List<ScenarioLesson>) { flow.value = lessons }
}

private class FakePracticeRepository : PracticeRepository {
    val insertedAttempts = mutableListOf<PracticeAttempt>()

    override fun getByChildId(childId: String): Flow<List<PracticeAttempt>> =
        MutableStateFlow(emptyList())

    override fun getRecentByChildId(childId: String, limit: Int): Flow<List<PracticeAttempt>> =
        MutableStateFlow(emptyList())

    override suspend fun insert(attempt: PracticeAttempt) {
        insertedAttempts += attempt
    }
}
