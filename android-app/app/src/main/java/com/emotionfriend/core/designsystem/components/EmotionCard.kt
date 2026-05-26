package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme

/**
 * Standard content card with large rounded corners and a soft elevation.
 * Accepts any composable content in its slot.
 */
@Composable
fun EmotionCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedColor = if (containerColor == Color.Unspecified)
        MaterialTheme.colorScheme.surfaceVariant
    else
        containerColor
    Card(
        shape   = MaterialTheme.shapes.large,
        colors  = CardDefaults.cardColors(containerColor = resolvedColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content  = content
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F0)
@Composable
private fun EmotionCardPreview() {
    EmotionFriendTheme {
        EmotionCard(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Đây là một thẻ nội dung",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text  = "Nội dung phụ có thể xuất hiện ở đây.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
