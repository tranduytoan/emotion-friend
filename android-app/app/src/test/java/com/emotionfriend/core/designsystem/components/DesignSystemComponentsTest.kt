package com.emotionfriend.core.designsystem.components

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DesignSystemComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEmotion = EmotionCard(
        id = "1",
        name = "Happy",
        emoji = "😊",
        type = EmotionType.HAPPY,
        description = "A joyful feeling"
    )

    @Test
    fun testEmotionCardDisplaysEmotion() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionCard(
                        card = testEmotion,
                        onClick = {},
                        isSelected = false
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Happy").assertIsDisplayed()
        composeTestRule.onNodeWithText("😊").assertIsDisplayed()
    }

    @Test
    fun testEmotionCardClickable() {
        val clickedEmotions = mutableListOf<EmotionCard>()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionCard(
                        card = testEmotion,
                        onClick = { clickedEmotions.add(testEmotion) },
                        isSelected = false
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Happy").performClick()

        assert(clickedEmotions.size == 1)
    }

    @Test
    fun testEmotionCardSelectedState() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionCard(
                        card = testEmotion,
                        onClick = {},
                        isSelected = true
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Happy").assertIsDisplayed()
    }

    @Test
    fun testEmotionPrimaryButtonDisplaysText() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionPrimaryButton(
                        text = "Continue",
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Continue").assertIsDisplayed()
    }

    @Test
    fun testEmotionPrimaryButtonClickable() {
        val clicked = mutableListOf<Unit>()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionPrimaryButton(
                        text = "Continue",
                        onClick = { clicked.add(Unit) }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Continue").performClick()

        assert(clicked.size == 1)
    }

    @Test
    fun testEmotionOptionButtonDisplaysOption() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    EmotionOptionButton(
                        text = "Option 1",
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Option 1").assertIsDisplayed()
    }

    @Test
    fun testTeacherMyAvatarDisplays() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    TeacherMyAvatar()
                }
            }
        }

        // Component should render without errors
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun testFeedbackBannerDisplaysMessage() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    FeedbackBanner(
                        message = "Great job!",
                        isSuccess = true
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Great job!").assertIsDisplayed()
    }

    @Test
    fun testFeedbackBannerSuccess() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    FeedbackBanner(
                        message = "Correct answer!",
                        isSuccess = true
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Correct answer!").assertIsDisplayed()
    }

    @Test
    fun testFeedbackBannerError() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    FeedbackBanner(
                        message = "Try again",
                        isSuccess = false
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Try again").assertIsDisplayed()
    }

    @Test
    fun testVyEmotionDisplays() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    VyEmotion(emotion = EmotionType.HAPPY)
                }
            }
        }

        // Component should render
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun testTeacherMyMessagesDisplaysMessage() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    TeacherMyMessages(
                        message = "Well done!"
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Well done!").assertIsDisplayed()
    }

    @Test
    fun testConfettiOverlayDisplays() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    ConfettiOverlay()
                }
            }
        }

        // Component should render without errors
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun testEmotionScreenScaffoldDisplays() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                EmotionScreenScaffold(
                    title = "Test Screen"
                ) {
                    // Content
                }
            }
        }

        composeTestRule.onNodeWithText("Test Screen").assertIsDisplayed()
    }
}
