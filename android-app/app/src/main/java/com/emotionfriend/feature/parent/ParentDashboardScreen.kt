package com.emotionfriend.feature.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    EmotionScreenScaffold(title = "Theo dõi con", onBack = onBack) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp),
                    color = SkyBlue40,
                )
            }

            else -> {
                Column(
                    modifier = modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // ── Child info card ───────────────────────────────────────
                    ChildInfoCard(
                        name   = state.childName,
                        age    = state.childAge,
                        avatar = state.childAvatar,
                    )

                    // ── Progress overview ─────────────────────────────────────
                    ProgressOverviewCard(state = state)

                    // ── Recent emotions ───────────────────────────────────────
                    if (state.recentEntries.isNotEmpty()) {
                        RecentEmotionCard(entries = state.recentEntries)
                    } else {
                        EmptyEmotionCard()
                    }

                    // ── Navigate to full report ───────────────────────────────
                    EmotionPrimaryButton(
                        text      = "Xem báo cáo chi tiết",
                        onClick   = onNavigateToReport,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Child info card
// ---------------------------------------------------------------------------

@Composable
private fun ChildInfoCard(
    name: String,
    age: Int,
    avatar: String,
    modifier: Modifier = Modifier,
) {
    EmotionCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.fillMaxWidth(),
        ) {
            Text(
                text     = avatar,
                fontSize = 52.sp,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text       = name,
                    style      = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "$age tuổi",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVar),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Progress overview card
// ---------------------------------------------------------------------------

@Composable
private fun ProgressOverviewCard(
    state: ParentDashboardUiState,
    modifier: Modifier = Modifier,
) {
    EmotionCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text     = "📊 Tiến trình học tập",
            style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp),
        )

        // Completed lessons
        StatRow(label = "Bài đã hoàn thành", value = "${state.completedLessons} bài")

        Spacer(modifier = Modifier.height(8.dp))

        // Accuracy rate
        StatRow(
            label = "Tỷ lệ trả lời đúng",
            value = "${(state.accuracyRate * 100).roundToInt()}%",
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress       = { state.accuracyRate.coerceIn(0f, 1f) },
            modifier       = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(MaterialTheme.shapes.small),
            color          = MintGreen40,
            trackColor     = MintGreen80,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Journal count
        StatRow(label = "Lần ghi cảm xúc", value = "${state.journalCount} lần")

        // Most mistaken emotion
        state.mostMistakenEmotion?.let { emotion ->
            Spacer(modifier = Modifier.height(8.dp))
            EmotionCard(
                modifier     = Modifier.fillMaxWidth(),
                containerColor = SkyBlueLight,
            ) {
                Text(
                    text  = "⚠️ Con hay nhầm: ${emotion.toVietnameseName()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Recent emotions card
// ---------------------------------------------------------------------------

@Composable
private fun RecentEmotionCard(
    entries: List<JournalEntry>,
    modifier: Modifier = Modifier,
) {
    EmotionCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text     = "📓 Cảm xúc gần đây",
            style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        entries.forEach { entry ->
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier             = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Text(text = entry.emotionType.toEmoji(), fontSize = 24.sp)
                Column {
                    Text(
                        text  = entry.emotionType.toVietnameseName(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    )
                    entry.note?.takeIf { it.isNotBlank() }?.let { note ->
                        Text(
                            text  = note,
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVar),
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Empty state card
// ---------------------------------------------------------------------------

@Composable
private fun EmptyEmotionCard(modifier: Modifier = Modifier) {
    EmotionCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text  = "📓 Con chưa ghi cảm xúc nào.\nHãy khuyến khích con ghi lại cảm xúc mỗi ngày! 💛",
            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVar),
        )
    }
}

// ---------------------------------------------------------------------------
// Helper: single stat row
// ---------------------------------------------------------------------------

@Composable
private fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVar))
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

// ---------------------------------------------------------------------------
// EmotionType helpers (local to parent feature)
// ---------------------------------------------------------------------------

private fun EmotionType.toVietnameseName() = when (this) {
    EmotionType.HAPPY     -> "Vui vẻ"
    EmotionType.SAD       -> "Buồn"
    EmotionType.ANGRY     -> "Tức giận"
    EmotionType.SURPRISED -> "Ngạc nhiên"
    EmotionType.CALM      -> "Bình tĩnh"
    EmotionType.TIRED     -> "Mệt mỏi"
}

private fun EmotionType.toEmoji() = when (this) {
    EmotionType.HAPPY     -> "😊"
    EmotionType.SAD       -> "😢"
    EmotionType.ANGRY     -> "😠"
    EmotionType.SURPRISED -> "😲"
    EmotionType.CALM      -> "😌"
    EmotionType.TIRED     -> "😴"
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun ParentDashboardPreview() {
    EmotionFriendTheme {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChildInfoCard(name = "Bé Minh", age = 8, avatar = "🧒")
            ProgressOverviewCard(
                state = ParentDashboardUiState(
                    isLoading        = false,
                    childName        = "Bé Minh",
                    childAge         = 8,
                    completedLessons = 12,
                    accuracyRate     = 0.75f,
                    journalCount     = 5,
                    mostMistakenEmotion = EmotionType.SURPRISED,
                ),
            )
        }
    }
}
