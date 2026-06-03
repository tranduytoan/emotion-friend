package com.emotionfriend.feature.progress

import com.emotionfriend.data.repository.ProgressRepository
import com.emotionfriend.domain.model.ProgressSummary
import com.emotionfriend.domain.model.EmotionType
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testProgress = ProgressSummary(
        id = 1,
        childId = "default_child",
        date = LocalDate.now(),
        totalAttempts = 10,
        correctAnswers = 8,
        accuracy = 80f,
        dominantEmotion = EmotionType.HAPPY,
        practiceMinutes = 15
    )

    private lateinit var progressRepo: FakeProgressRepository
    private lateinit var viewModel: ProgressViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        progressRepo = FakeProgressRepository(listOf(testProgress))
        viewModel = ProgressViewModel(progressRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads progress summary on init`() = runTest {
        val summary = viewModel.progressSummary.value
        assertNotNull("Progress summary should be loaded", summary)
    }

    @Test
    fun `accuracy percentage is calculated correctly`() = runTest {
        val accuracy = viewModel.accuracyPercentage.value
        assertEquals(80f, accuracy, 0.1f)
    }

    @Test
    fun `dominant emotion is tracked`() = runTest {
        val emotion = viewModel.dominantEmotion.value
        assertEquals(EmotionType.HAPPY, emotion)
    }

    @Test
    fun `practice minutes are tracked`() = runTest {
        val minutes = viewModel.practiceMinutes.value
        assertEquals(15, minutes)
    }

    @Test
    fun `streak calculation works`() = runTest {
        val streak = viewModel.practiceStreak.value
        assertNotNull("Streak should be calculated", streak)
        assertTrue("Streak should be non-negative", streak!! >= 0)
    }

    @Test
    fun `improvements are calculated from historical data`() = runTest {
        val improvements = viewModel.recentImprovements.value
        assertNotNull("Improvements should be calculated", improvements)
    }

    @Test
    fun `achievement badges are awarded for milestones`() = runTest {
        val badges = viewModel.achievementBadges.value
        assertNotNull("Achievement badges should be available", badges)
    }

    @Test
    fun `total attempts is tracked`() = runTest {
        val attempts = viewModel.totalAttempts.value
        assertEquals(10, attempts)
    }

    @Test
    fun `correct answers count is tracked`() = runTest {
        val correct = viewModel.correctAnswers.value
        assertEquals(8, correct)
    }
}

private class FakeProgressRepository(
    private val progressList: List<ProgressSummary>
) : ProgressRepository {
    override fun getByChildId(childId: String): Flow<List<ProgressSummary>> =
        MutableStateFlow(progressList)

    override fun getLatestByChildId(childId: String): Flow<ProgressSummary?> =
        MutableStateFlow(progressList.lastOrNull())

    override suspend fun insert(summary: ProgressSummary) {}
}
