package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OutlineLight
import com.emotionfriend.core.designsystem.theme.dimensions

/**
 * Pill-shaped progress indicator for multi-step activities.
 *
 * Shows individual step dots (completed = filled, current = accent, upcoming = outline).
 * Also shows a "current / total" label inside a pill container.
 *
 * @param current  1-based index of the current step
 * @param total    Total number of steps
 */
@Composable
fun ProgressPill(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val description = "Câu $current trên $total"
    Row(
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spacingSm),
        modifier = modifier
            .wrapContentWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MintGreen80.copy(alpha = 0.5f))
            .padding(horizontal = MaterialTheme.dimensions.spacingMd, vertical = MaterialTheme.dimensions.spacingXs)
            .semantics { contentDescription = description }
    ) {
        // Step dots
        for (step in 1..total) {
            val isCompleted = step < current
            val isCurrent   = step == current
            Box(
                modifier = Modifier
                    .size(if (isCurrent) MaterialTheme.dimensions.progressDotSizeActive else MaterialTheme.dimensions.progressDotSize)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        when {
                            isCompleted -> MintGreen40
                            isCurrent   -> MintGreen40
                            else        -> OutlineLight
                        }
                    )
            )
        }

        // Text label
        Text(
            text  = "$current / $total",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F0)
@Composable
private fun ProgressPillPreview() {
    EmotionFriendTheme {
        ProgressPill(
            current  = 3,
            total    = 5,
            modifier = Modifier.padding(16.dp)
        )
    }
}
