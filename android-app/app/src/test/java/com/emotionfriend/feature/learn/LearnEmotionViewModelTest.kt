package com.emotionfriend.feature.learn

import com.emotionfriend.data.repository.EmotionRepository
import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
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
class LearnEmotionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    // Fixed set of 3 distinct cards — fully covers option list building (needs ≥ 4 types total)
    private val testCards = listOf(
        EmotionCard("1", "Vui",  "😊", EmotionType.HAPPY,    "Cảm xúc vui vẻ"),
        EmotionCard("2", "Buồn", "😢", EmotionType.SAD,      "Cảm xúc buồn bã"),
        EmotionCard("3", "Tức",  "😠", EmotionType.ANGRY,    "Cảm xúc tức giận"),
        EmotionCard("4", "Ngạc","😲", EmotionType.SURPRISED, "Cảm xúc ngạc nhiên"),
    )

    private lateinit var emotionRepo: FakeEmotionRepository
    private lateinit var practiceRepo: FakePracticeRepository
    private lateinit var viewModel: LearnEmotionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        emotionRepo = FakeEmotionRepository(testCards)
        practiceRepo = FakePracticeRepository()
        viewModel = LearnEmotionViewModel(emotionRepo, practiceRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -----------------------------------------------------------------------
    // Test case 1 — Initial state loads first question
    // -----------------------------------------------------------------------

    @Test
    fun `initial state loads first question`() = runTest {
        val state = viewModel.uiState.value

        assertFalse("Should not still be loading", state.isLoading)
        assertNotNull("currentCard should be set", state.currentCard)
        assertTrue("options list should not be empty", state.options.isNotEmpty())
        assertEquals("questionIndex should start at 0", 0, state.questionIndex)
        assertEquals("totalQuestions should equal number of cards", testCards.size, state.totalQuestions)
        assertFalse("Session should not be complete at start", state.isSessionComplete)
    }

    // -----------------------------------------------------------------------
    // Test case 2 — Selecting the CORRECT answer and submitting → isCorrect = true
    // -----------------------------------------------------------------------

    @Test
    fun `selecting correct answer and submitting sets isCorrect true`() = runTest {
        val correctEmotion = viewModel.uiState.value.currentCard!!.type

        viewModel.selectAnswer(correctEmotion)
        viewModel.submitAnswer()

        val state = viewModel.uiState.value
        assertTrue("Answer should be marked submitted", state.isAnswerSubmitted)
        assertEquals(true, state.isCorrect)
        assertTrue("Feedback message should not be empty", state.feedbackMessage.isNotEmpty())
    }

    // -----------------------------------------------------------------------
    // Test case 3 — Selecting a WRONG answer and submitting → isCorrect = false
    // -----------------------------------------------------------------------

    @Test
    fun `selecting wrong answer and submitting sets isCorrect false`() = runTest {
        val correctEmotion = viewModel.uiState.value.currentCard!!.type
        val wrongEmotion = EmotionType.entries.first { it != correctEmotion }

        viewModel.selectAnswer(wrongEmotion)
        viewModel.submitAnswer()

        val state = viewModel.uiState.value
        assertTrue("Answer should be marked submitted", state.isAnswerSubmitted)
        assertEquals(false, state.isCorrect)
        assertTrue("Feedback message should not be empty", state.feedbackMessage.isNotEmpty())
    }

    // -----------------------------------------------------------------------
    // Test case 4 — Submitting an answer saves a PracticeAttempt
    // -----------------------------------------------------------------------

    @Test
    fun `submitting answer saves a PracticeAttempt to repository`() = runTest {
        val card = viewModel.uiState.value.currentCard!!

        viewModel.selectAnswer(card.type)
        viewModel.submitAnswer()

        assertEquals("Exactly one attempt should be saved", 1, practiceRepo.insertedAttempts.size)

        val attempt = practiceRepo.insertedAttempts.first()
        assertEquals("promptId should match card id", card.id, attempt.promptId)
        assertEquals("correctEmotion should match card type", card.type, attempt.correctEmotion)
        assertEquals("selectedEmotion should match what was selected", card.type, attempt.selectedEmotion)
        assertEquals("isCorrect should be true for correct answer", true, attempt.isCorrect)
        assertEquals("taskType should be learn_emotion", "learn_emotion", attempt.taskType)
    }

    // -----------------------------------------------------------------------
    // Test case 5 — Calling nextQuestion() advances to the next card
    // -----------------------------------------------------------------------

    @Test
    fun `calling nextQuestion advances to the next card`() = runTest {
        val firstCard = viewModel.uiState.value.currentCard!!

        // Must submit an answer before calling next
        viewModel.selectAnswer(firstCard.type)
        viewModel.submitAnswer()
        viewModel.nextQuestion()

        val state = viewModel.uiState.value
        assertEquals("questionIndex should advance to 1", 1, state.questionIndex)
        assertNotNull("New card should be set", state.currentCard)
        assertFalse("isAnswerSubmitted should be reset", state.isAnswerSubmitted)
        assertNull("selectedEmotion should be cleared", state.selectedEmotion)
        assertNull("isCorrect should be reset", state.isCorrect)
        assertFalse("Session should not be complete yet", state.isSessionComplete)
    }
}

// ---------------------------------------------------------------------------
// Fake repositories — no framework, no mocking library needed
// ---------------------------------------------------------------------------

private class FakeEmotionRepository(
    cards: List<EmotionCard>
) : EmotionRepository {
    private val flow = MutableStateFlow(cards)

    override fun getAll(): Flow<List<EmotionCard>> = flow
    override suspend fun getById(id: String): EmotionCard? = flow.value.find { it.id == id }
    override suspend fun upsertAll(cards: List<EmotionCard>) { flow.value = cards }
    override fun getByType(type: EmotionType): Flow<List<EmotionCard>> =
        MutableStateFlow(flow.value.filter { it.type == type })
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
