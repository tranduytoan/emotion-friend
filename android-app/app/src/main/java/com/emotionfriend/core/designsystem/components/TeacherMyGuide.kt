package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable companion row: [TeacherMyAvatar] + [TeacherMySpeechBubble].
 *
 * Drop this anywhere in a screen to show Cô giáo My alongside a short message.
 *
 * @param message  What the companion says (1–2 lines, keep gentle).
 * @param onSpeak  Called when the user taps 🔊 replay; pass null to hide the button.
 */
@Composable
fun TeacherMyGuide(
    message : String,
    onSpeak : (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
    ) {
        TeacherMyAvatar()
        Spacer(Modifier.width(8.dp))
        TeacherMySpeechBubble(
            message  = message,
            onSpeak  = onSpeak,
            modifier = Modifier.weight(1f),
        )
    }
}
