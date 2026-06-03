package com.emotionfriend.feature.learn

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class LearnScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCards = listOf(
        EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "Happy emotion"),
        EmotionCard("2", "Sad", "😢", EmotionType.SAD, "Sad emotion"),
        EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "Angry emotion"),
    )

    @Test
    fun testLearnScreenDisplaysCurrentCard() {
        val viewModel = FakeLearnViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LearnScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithText("Happy").assertIsDisplayed()
    }

    @Test
    fun testLearnScreenDisplaysQuestionProgress() {
        val viewModel = FakeLearnViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LearnScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithText("1 / 3").assertIsDisplayed()
    }

    @Test
    fun testLearnScreenCanSelectAnswer() {
        val viewModel = FakeLearnViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LearnScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithTag("option_happy").performClick()

        // Verify selection was registered
        assert(viewModel.lastSelectedEmotion == EmotionType.HAPPY)
    }

    @Test
    fun testLearnScreenShowsSubmitButton() {
        val viewModel = FakeLearnViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LearnScreen(viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithText("Gửi").assertIsDisplayed()
    }

    @Test
    fun testLearnScreenProgressesOnNext() {
        val viewModel = FakeLearnViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LearnScreen(viewModel = viewModel)
                }
            }
        }

        // Answer first question
        composeTestRule.onNodeWithTag("option_happy").performClick()
        composeTestRule.onNodeWithText("Gửi").performClick()

        // Move to next
        composeTestRule.onNodeWithText("Tiếp theo").performClick()

        // Should show next card
        assert(viewModel.currentQuestionIndex == 1)
    }
}

private class FakeLearnViewModel {
    var lastSelectedEmotion: EmotionType? = null
    var currentQuestionIndex = 0

    val uiState = MutableStateFlow(LearnUiState(
        currentCard = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "Happy"),
        questionIndex = 0,
        totalQuestions = 3,
        options = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.ANGRY),
        isLoading = false
    ))

    fun selectAnswer(emotion: EmotionType) {
        lastSelectedEmotion = emotion
    }

    fun submitAnswer() {}

    fun nextQuestion() {
        currentQuestionIndex++
        uiState.value = uiState.value.copy(questionIndex = currentQuestionIndex)
    }
}

data class LearnUiState(
    val currentCard: EmotionCard? = null,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val options: List<EmotionType> = emptyList(),
    val selectedEmotion: EmotionType? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean? = null,
    val feedbackMessage: String = "",
    val isLoading: Boolean = false,
    val isSessionComplete: Boolean = false,
)
