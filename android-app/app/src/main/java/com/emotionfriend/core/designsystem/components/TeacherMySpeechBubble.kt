package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SurfaceVariant

/**
 * A light speech bubble showing a short message from "Cô giáo My".
 *
 * Optionally displays a 🔊 replay button when [onSpeak] is provided.
 * The button is intentionally small and unobtrusive so the text stays primary.
 *
 * @param message  Text to display (keep to 1–2 lines).
 * @param onSpeak  Called when the user taps the replay button. Pass null to hide it.
 */
@Composable
fun TeacherMySpeechBubble(
    message : String,
    onSpeak : (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape     = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text     = message,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            if (onSpeak != null) {
                IconButton(
                    onClick  = onSpeak,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Filled.VolumeUp,
                        contentDescription = "Nghe lại",
                        tint               = SkyBlue40,
                        modifier           = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
