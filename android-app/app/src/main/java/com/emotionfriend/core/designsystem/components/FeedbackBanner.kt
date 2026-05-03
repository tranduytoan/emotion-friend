package com.emotionfriend.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.FeedbackCorrect
import com.emotionfriend.core.designsystem.theme.FeedbackCorrectBg
import com.emotionfriend.core.designsystem.theme.FeedbackWrong
import com.emotionfriend.core.designsystem.theme.FeedbackWrongBg

enum class FeedbackType { CORRECT, WRONG }

/**
 * Animated banner that slides in from the bottom to show answer feedback.
 *
 * Uses LiveRegion semantics so TalkBack announces the result automatically.
 *
 * @param visible      Whether the banner is shown
 * @param type         CORRECT (green) or WRONG (red)
 * @param message      Short feedback message, e.g. "Đúng rồi! 🎉"
 */
@Composable
fun FeedbackBanner(
    visible: Boolean,
    type: FeedbackType,
    message: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (type == FeedbackType.CORRECT) FeedbackCorrectBg else FeedbackWrongBg
    val accentColor     = if (type == FeedbackType.CORRECT) FeedbackCorrect   else FeedbackWrong
    val emoji           = if (type == FeedbackType.CORRECT) "🎉" else "💪"

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn() + slideInVertically { it },
        exit    = fadeOut() + slideOutVertically { it },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(backgroundColor)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text  = message,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F0)
@Composable
private fun FeedbackBannerCorrectPreview() {
    EmotionFriendTheme {
        FeedbackBanner(
            visible  = true,
            type     = FeedbackType.CORRECT,
            message  = "Đúng rồi! Bạn giỏi lắm!",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F0)
@Composable
private fun FeedbackBannerWrongPreview() {
    EmotionFriendTheme {
        FeedbackBanner(
            visible  = true,
            type     = FeedbackType.WRONG,
            message  = "Thử lại nhé, bạn làm được!",
            modifier = Modifier.padding(16.dp)
        )
    }
}
