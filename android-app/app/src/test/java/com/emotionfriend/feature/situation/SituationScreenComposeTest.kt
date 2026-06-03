package com.emotionfriend.feature.situation

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.ScenarioLesson
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SituationScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScenarios = listOf(
        ScenarioLesson(
            id = "1",
            title = "Scenario 1",
            situationText = "Bạn nhận được quà",
            imageName = null,
            correctEmotion = EmotionType.HAPPY,
            options = listOf(EmotionType.HAPPY, EmotionType.SAD),
            explanation = "Receive gift makes you happy"
        ),
        ScenarioLesson(
            id = "2",
            title = "Scenario 2",
            situationText = "Bạn bị mất đồ chơi",
            imageName = null,
            correctEmotion = EmotionType.ANGRY,
            options = listOf(EmotionType.ANGRY, EmotionType.HAPPY),
            explanation = "Lost toy makes you angry"
        ),
    )

    @Test
    fun testSituationScreenDisplaysScenario() {
        val viewModel = FakeSituationViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    SituationScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithText("Bạn nhận được quà").assertIsDisplayed()
    }

    @Test
    fun testSituationScreenDisplaysOptions() {
        val viewModel = FakeSituationViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    SituationScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithTag("emotion_option_happy").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emotion_option_sad").assertIsDisplayed()
    }

    @Test
    fun testSituationScreenCanSelectEmotion() {
        val viewModel = FakeSituationViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    SituationScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithTag("emotion_option_happy").performClick()

        assert(viewModel.selectedEmotion == EmotionType.HAPPY)
    }

    @Test
    fun testSituationScreenShowsExplanationAfterAnswer() {
        val viewModel = FakeSituationViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    SituationScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithTag("emotion_option_happy").performClick()
        composeTestRule.onNodeWithText("Gửi").performClick()

        composeTestRule.onNodeWithText("Receive gift makes you happy").assertIsDisplayed()
    }

    @Test
    fun testSituationScreenProgresses() {
        val viewModel = FakeSituationViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    SituationScreen(viewModel = viewModel)
                }
            }
        }

        // Answer first scenario
        composeTestRule.onNodeWithTag("emotion_option_happy").performClick()
        composeTestRule.onNodeWithText("Gửi").performClick()

        // Move to next
        composeTestRule.onNodeWithText("Tiếp theo").performClick()

        assert(viewModel.currentIndex == 1)
    }
}

private class FakeSituationViewModel {
    var selectedEmotion: EmotionType? = null
    var currentIndex = 0

    val uiState = MutableStateFlow(SituationUiState(
        currentScenario = ScenarioLesson(
            id = "1",
            title = "Scenario 1",
            situationText = "Bạn nhận được quà",
            imageName = null,
            correctEmotion = EmotionType.HAPPY,
            options = listOf(EmotionType.HAPPY, EmotionType.SAD),
            explanation = "Receive gift makes you happy"
        ),
        questionIndex = 0,
        totalQuestions = 2
    ))

    fun selectEmotion(emotion: EmotionType) {
        selectedEmotion = emotion
    }

    fun submitAnswer() {}

    fun nextScenario() {
        currentIndex++
    }
}

data class SituationUiState(
    val currentScenario: ScenarioLesson? = null,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedEmotion: EmotionType? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean? = null,
    val explanation: String = "",
    val isLoading: Boolean = false,
    val isSessionComplete: Boolean = false,
)
