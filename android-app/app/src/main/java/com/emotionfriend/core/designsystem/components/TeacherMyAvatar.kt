package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionfriend.core.designsystem.theme.SunYellow80

/**
 * Circular avatar for "Cô giáo Vy".
 *
 * Displays a different emoji/image per [VyEmotion] state.
 * When real image files are added to res/drawable/ (vy_neutral.png, vy_excited.png, etc.)
 * replace the emoji fallback in [vyEmotionEmoji] with Image composable calls.
 *
 * @param size     Diameter in dp (default 64).
 * @param emotion  Current emotional state — drives which image/emoji is shown.
 */
@Composable
fun TeacherMyAvatar(
    modifier: Modifier = Modifier,
    size: Int = 64,
    emotion: VyEmotion = VyEmotion.NEUTRAL,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(SunYellow80),
    ) {
        // TODO: Replace with Image(painterResource(vyEmotionDrawable(emotion))) when
        //       image files are added to res/drawable/vy_*.png
        Text(
            text     = vyEmotionEmoji(emotion),
            fontSize = (size * 0.52f).sp,
        )
    }
}

/** Maps VyEmotion to a placeholder emoji until designer images are ready. */
private fun vyEmotionEmoji(emotion: VyEmotion): String = when (emotion) {
    VyEmotion.NEUTRAL      -> "👩‍🏫"
    VyEmotion.EXCITED      -> "🥳"
    VyEmotion.HAPPY        -> "😄"
    VyEmotion.ENCOURAGING  -> "🤗"
    VyEmotion.CALM         -> "😌"
    VyEmotion.CELEBRATING  -> "🎉"
}
