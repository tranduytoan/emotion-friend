package com.emotionfriend.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme

// ---------------------------------------------------------------------------
// Data model (private, UI-only)
// ---------------------------------------------------------------------------

private data class HomeFeature(
    val emoji: String,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToSituation: () -> Unit,
    onNavigateToExpress: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val features = listOf(
        HomeFeature(
            emoji       = "📚",
            title       = "Học cảm xúc",
            description = "Tìm hiểu các cảm xúc qua hình ảnh và thẻ bài.",
            onClick     = onNavigateToLearn
        ),
        HomeFeature(
            emoji       = "🤝",
            title       = "Hiểu tình huống",
            description = "Luyện nhận biết cảm xúc trong các tình huống thực tế.",
            onClick     = onNavigateToSituation
        ),
        HomeFeature(
            emoji       = "😊",
            title       = "Luyện biểu cảm",
            description = "Thực hành biểu lộ cảm xúc qua khuôn mặt.",
            onClick     = onNavigateToExpress
        ),
        HomeFeature(
            emoji       = "🌈",
            title       = "Thư giãn",
            description = "Thở sâu và nghỉ ngơi khi cảm thấy căng thẳng.",
            onClick     = onNavigateToRelax
        ),
        HomeFeature(
            emoji       = "📓",
            title       = "Cảm xúc của con",
            description = "Ghi lại cảm xúc của con hôm nay.",
            onClick     = onNavigateToJournal
        ),
        HomeFeature(
            emoji       = "🌟",
            title       = "Tiến trình",
            description = "Xem con đã học được bao nhiêu rồi!",
            onClick     = onNavigateToProgress
        )
    )

    EmotionScreenScaffold {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Greeting header
            Text(
                text     = "Chào con! 👋",
                style    = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text  = "Hôm nay con muốn học gì?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature cards
            features.forEach { feature ->
                FeatureCard(feature = feature)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Feature card
// ---------------------------------------------------------------------------

@Composable
private fun FeatureCard(
    feature: HomeFeature,
    modifier: Modifier = Modifier
) {
    EmotionCard(
        modifier = modifier.clickable(
            role    = Role.Button,
            onClickLabel = feature.title,
            onClick = feature.onClick
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.fillMaxWidth()
        ) {
            Text(
                text     = feature.emoji,
                fontSize = 40.sp,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text  = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = feature.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    EmotionFriendTheme {
        HomeScreen(
            onNavigateToLearn     = {},
            onNavigateToSituation = {},
            onNavigateToExpress   = {},
            onNavigateToRelax     = {},
            onNavigateToJournal   = {},
            onNavigateToProgress  = {}
        )
    }
}
