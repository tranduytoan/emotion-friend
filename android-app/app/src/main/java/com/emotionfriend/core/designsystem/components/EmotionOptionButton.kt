package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.EmotionAngry
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg

/**
 * A large tappable option button used in emotion-choice screens.
 *
 * @param label        Short emotion label (e.g. "Vui vẻ")
 * @param emoji        Emoji or icon character shown above the label
 * @param selected     Whether this option is currently selected
 * @param containerColor Background color (emotion-specific, e.g. EmotionHappyBg)
 * @param borderColor  Border / accent color (emotion-specific, e.g. EmotionHappy)
 * @param showLabel    When false only the emoji is shown — use for audio-first screens
 * @param onClick      Callback when tapped
 */
@Composable
fun EmotionOptionButton(
    label: String,
    emoji: String,
    selected: Boolean,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick  = onClick,
        shape    = MaterialTheme.shapes.large,
        border   = BorderStroke(
            width = if (selected) 3.dp else 1.5.dp,
            color = if (selected) borderColor else borderColor.copy(alpha = 0.4f)
        ),
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) containerColor else containerColor.copy(alpha = 0.5f),
            contentColor   = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
            .heightIn(min = 100.dp)
            .semantics {
                role = Role.Button
                contentDescription = if (selected) "$label, đã chọn" else label
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.displayMedium)
            if (showLabel) {
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = label, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F0)
@Composable
private fun EmotionOptionButtonPreview() {
    EmotionFriendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmotionOptionButton(
                label          = "Vui vẻ",
                emoji          = "😄",
                selected       = true,
                containerColor = EmotionHappyBg,
                borderColor    = EmotionHappy,
                onClick        = {},
                modifier       = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(12.dp))
            EmotionOptionButton(
                label          = "Tức giận",
                emoji          = "😠",
                selected       = false,
                containerColor = EmotionAngryBg,
                borderColor    = EmotionAngry,
                onClick        = {},
                modifier       = Modifier.fillMaxWidth()
            )
        }
    }
}
