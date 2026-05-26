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
 * Circular avatar representing "Cô giáo My".
 *
 * Intentionally simple — a soft yellow circle with a teacher emoji.
 * No strong animation to avoid sensory overload for autistic children.
 *
 * @param size Diameter in dp (default 48).
 */
@Composable
fun TeacherMyAvatar(
    modifier: Modifier = Modifier,
    size: Int = 48,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(SunYellow80),
    ) {
        Text(
            text     = "👩‍🏫",
            fontSize = (size * 0.55f).sp,
        )
    }
}
