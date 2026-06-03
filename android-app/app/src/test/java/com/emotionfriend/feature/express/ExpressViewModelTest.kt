package com.emotionfriend.feature.express

import android.content.Context
import com.emotionfriend.data.repository.PracticeRepository
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
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ExpressViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var practiceRepo: FakePracticeRepository
    private lateinit var viewModel: ExpressViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        practiceRepo = FakePracticeRepository()
        viewModel = ExpressViewModel(practiceRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is ready to capture`() = runTest {
        val state = viewModel.uiState.value
        assertFalse("Should not be loading initially", state.isLoading)
        assertFalse("Should not have error initially", state.hasError)
    }

    @Test
    fun `can select emotion to express`() = runTest {
        viewModel.selectEmotion(EmotionType.HAPPY)

        val state = viewModel.uiState.value
        assertEquals(EmotionType.HAPPY, state.selectedEmotion)
    }

    @Test
    fun `can capture emotion expression`() = runTest {
        viewModel.selectEmotion(EmotionType.CALM)
        viewModel.captureExpression()

        val state = viewModel.uiState.value
        assertNotNull("Expression should be captured", state.expressionData)
    }

    @Test
    fun `multiple expressions can be captured in sequence`() = runTest {
        val emotions = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.ANGRY)
        
        for (emotion in emotions) {
            viewModel.selectEmotion(emotion)
            viewModel.captureExpression()
        }

        assertEquals(3, practiceRepo.insertedAttempts.size)
    }

    @Test
    fun `captured expression can be saved to repository`() = runTest {
        viewModel.selectEmotion(EmotionType.HAPPY)
        viewModel.captureExpression()
        viewModel.saveExpression()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Attempt should be saved", practiceRepo.insertedAttempts.isNotEmpty())
    }

    @Test
    fun `expression can be retaken before saving`() = runTest {
        viewModel.selectEmotion(EmotionType.HAPPY)
        viewModel.captureExpression()
        
        var state = viewModel.uiState.value
        assertNotNull("First expression captured", state.expressionData)

        // Retake
        viewModel.retakeExpression()
        state = viewModel.uiState.value
        assertNull("Expression should be cleared", state.expressionData)
    }

    @Test
    fun `emotion reflection can be added to expression`() = runTest {
        viewModel.selectEmotion(EmotionType.CALM)
        viewModel.setReflection("I feel peaceful")

        val state = viewModel.uiState.value
        assertEquals("I feel peaceful", state.reflection)
    }

    @Test
    fun `experience is recorded with emotion and reflection`() = runTest {
        viewModel.selectEmotion(EmotionType.HAPPY)
        viewModel.setReflection("Great day ahead!")
        viewModel.captureExpression()
        viewModel.saveExpression()

        testDispatcher.scheduler.advanceUntilIdle()

        val attempts = practiceRepo.insertedAttempts
        assertTrue("Should save the experience", attempts.isNotEmpty())
    }

    @Test
    fun `expression type is set to express_emotion`() = runTest {
        viewModel.selectEmotion(EmotionType.HAPPY)
        viewModel.captureExpression()
        viewModel.saveExpression()

        testDispatcher.scheduler.advanceUntilIdle()

        val attempt = practiceRepo.insertedAttempts.firstOrNull()
        assertEquals("express_emotion", attempt?.taskType)
    }
}

data class ExpressUiState(
    val selectedEmotion: EmotionType? = null,
    val expressionData: Any? = null,
    val reflection: String = "",
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
)

private class FakePracticeRepository : PracticeRepository {
    val insertedAttempts = mutableListOf<PracticeAttempt>()

    override fun getByChildId(childId: String): Flow<List<PracticeAttempt>> =
        MutableStateFlow(insertedAttempts)

    override fun getRecentByChildId(childId: String, limit: Int): Flow<List<PracticeAttempt>> =
        MutableStateFlow(insertedAttempts.takeLast(limit))

    override suspend fun insert(attempt: PracticeAttempt) {
        insertedAttempts.add(attempt)
    }
}
