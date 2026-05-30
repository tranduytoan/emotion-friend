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
 * Drop this anywhere in a screen to show Cô giáo Vy alongside a short message.
 *
 * @param message    What the companion says (used for TTS; not shown as text).
 * @param onSpeak    Called when the user taps 🔊 replay; pass null to hide the button.
 * @param vyEmotion  Current emotion state — drives which avatar image/emoji is shown.
 */
@Composable
fun TeacherMyGuide(
    message   : String,
    onSpeak   : (() -> Unit)? = null,
    vyEmotion : VyEmotion = VyEmotion.NEUTRAL,
    modifier  : Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        TeacherMyAvatar(emotion = vyEmotion)
        Spacer(Modifier.width(8.dp))
        TeacherMySpeechBubble(
            message  = message,
            onSpeak  = onSpeak,
            modifier = Modifier.weight(1f),
        )
    }
}

