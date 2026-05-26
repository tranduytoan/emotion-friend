package com.emotionfriend.feature.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Screen — uses same ViewModel as ParentDashboard (same data, more detail)
// ---------------------------------------------------------------------------

@Composable
fun ParentReportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    EmotionScreenScaffold(title = "Báo cáo chi tiết", onBack = onBack) {
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
                // Use LazyColumn so journal history can be arbitrarily long.
                // weight(1f) is required because we're inside an EmotionScreenScaffold Column.
                LazyColumn(
                    modifier            = modifier
                        .weight(1f)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // ── Stats section ─────────────────────────────────────────
                    item {
                        ReportStatsCard(state = state)
                    }

                    // ── Most mistaken emotion ─────────────────────────────────
                    state.mostMistakenEmotion?.let { emotion ->
                        item {
                            EmotionCard(
                                modifier       = Modifier.fillMaxWidth(),
                                containerColor = SkyBlueLight,
                            ) {
                                Text(
                                    text  = "⚠️ Cảm xúc con hay nhầm nhất: ${emotion.toVietnameseName()} ${emotion.toEmoji()}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text  = "Hãy luyện tập thêm bài học về cảm xúc này cùng con nhé! 💪",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVar),
                                )
                            }
                        }
                    }

                    // ── Journal history header ────────────────────────────────
                    item {
                        Text(
                            text     = "📓 Lịch sử cảm xúc (${state.journalCount} lần)",
                            style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    // ── Journal entries list ──────────────────────────────────
                    if (state.allEntries.isEmpty()) {
                        item {
                            EmotionCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text  = "Con chưa ghi cảm xúc nào. Hãy khuyến khích con ghi lại mỗi ngày!",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVar),
                                )
                            }
                        }
                    } else {
                        item {
                            EmotionCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                    state.allEntries.forEachIndexed { index, entry ->
                                        JournalEntryRow(entry = entry)
                                        if (index < state.allEntries.lastIndex) {
                                            HorizontalDivider(
                                                modifier  = Modifier.padding(vertical = 8.dp),
                                                thickness = 0.5.dp,
                                                color     = MaterialTheme.colorScheme.outlineVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Encouragement footer ─────────────────────────────────
                    item {
                        val message = when {
                            state.accuracyRate >= 0.8f ->
                                "🌟 Con đang tiến bộ rất tốt! Phụ huynh hãy khen ngợi con nhiều hơn nhé."
                            state.completedLessons == 0 ->
                                "📚 Con chưa bắt đầu bài học nào. Hãy cùng con khám phá app nhé!"
                            else ->
                                "💛 Hãy tiếp tục cùng con luyện tập mỗi ngày một chút nhé."
                        }
                        EmotionCard(
                            modifier       = Modifier.fillMaxWidth(),
                            containerColor = MintGreen80,
                        ) {
                            Text(
                                text  = message,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Stats section
// ---------------------------------------------------------------------------

@Composable
private fun ReportStatsCard(
    state: ParentDashboardUiState,
    modifier: Modifier = Modifier,
) {
    EmotionCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text     = "📊 Tổng quan tiến trình",
            style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp),
        )

        ReportStatRow(label = "Bài đã hoàn thành",  value = "${state.completedLessons} bài")
        Spacer(modifier = Modifier.height(10.dp))
        ReportStatRow(
            label = "Tỷ lệ trả lời đúng",
            value = "${(state.accuracyRate * 100).roundToInt()}%",
        )
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress       = { state.accuracyRate.coerceIn(0f, 1f) },
            modifier       = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(MaterialTheme.shapes.small),
            color          = MintGreen40,
            trackColor     = MintGreen80,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ReportStatRow(label = "Tổng lần ghi cảm xúc", value = "${state.journalCount} lần")
    }
}

@Composable
private fun ReportStatRow(
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
// Journal entry row
// ---------------------------------------------------------------------------

@Composable
private fun JournalEntryRow(
    entry: JournalEntry,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(text = entry.emotionType.toEmoji(), fontSize = 28.sp)
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text  = entry.emotionType.toVietnameseName(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )
                Text(
                    text  = entry.createdAt.toFormattedDate(),
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVar),
                )
            }
            entry.note?.takeIf { it.isNotBlank() }?.let { note ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text  = note,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVar),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers (local, duplicated intentionally — parent feature is self-contained)
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

private fun Long.toFormattedDate(): String = runCatching {
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
}.getOrElse { "—" }

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun ParentReportPreview() {
    EmotionFriendTheme {
        ReportStatsCard(
            state = ParentDashboardUiState(
                isLoading        = false,
                childName        = "Bé Minh",
                completedLessons = 12,
                accuracyRate     = 0.75f,
                journalCount     = 8,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
