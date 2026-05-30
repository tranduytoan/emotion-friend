package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SurfaceVariant

/**
 * Speech bubble for "Cô giáo Vy".
 *
 * Displays "Cô giáo Vy:" label followed by a 🔊 audio replay button.
 * The [message] is spoken via TTS when the button is tapped but NOT shown as text
 * — keeping the UI clean and non-distracting for children with ASD.
 *
 * @param message  TTS content read aloud when the user taps the audio button.
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
            modifier              = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text  = "Cô giáo Vy:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SkyBlue40,
            )
            if (onSpeak != null) {
                IconButton(
                    onClick  = onSpeak,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Filled.VolumeUp,
                        contentDescription = "Nghe cô Vy nói",
                        tint               = SkyBlue40,
                        modifier           = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
