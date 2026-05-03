package com.emotionfriend.feature.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionAngry
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionSad
import com.emotionfriend.core.designsystem.theme.EmotionSurprised
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.domain.model.EmotionType
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    EmotionScreenScaffold(title = "Tiến trình", onBack = onBack) {
        when {
            state.isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.completedLessons == 0 && state.journalCount == 0 -> {
                EmptyProgressContent(modifier = modifier)
            }

            else -> {
                ProgressContent(state = state, modifier = modifier)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Progress content
// ---------------------------------------------------------------------------

@Composable
private fun ProgressContent(
    state: ProgressUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- Encouragement banner -------------------------------------------
        EmotionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💬", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(12.dp))
                Text(
                    text  = state.encouragementMessage,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // --- Stats row (lessons + journal) ----------------------------------
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                icon  = "📚",
                label = "Bài đã học",
                value = "${state.completedLessons}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon  = "📓",
                label = "Nhật ký",
                value = "${state.journalCount}",
                modifier = Modifier.weight(1f)
            )
        }

        // --- Accuracy card --------------------------------------------------
        EmotionCard {
            Text(
                text  = "Độ chính xác",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.SpaceBetween,
                modifier               = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = "${(state.accuracyRate * 100).roundToInt()}%",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = if (state.accuracyRate >= 0.8f) MintGreen40 else SkyBlue40
                    )
                )
                Text(
                    text  = accuracyLabel(state.accuracyRate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVar
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { state.accuracyRate.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                color            = if (state.accuracyRate >= 0.8f) MintGreen40 else SkyBlue40,
                trackColor       = MintGreen80.copy(alpha = 0.3f)
            )
        }

        // --- Most mistaken emotion ------------------------------------------
        state.mostMistakenEmotion?.let { emotion ->
            EmotionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = emotion.toEmoji(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text  = "Cảm xúc hay nhầm nhất",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVar
                        )
                        Text(
                            text  = emotion.toLabel(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // --- Parent/teacher suggestion card ---------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(SkyBlueLight)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text  = "💡 Gợi ý cho ba mẹ",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = parentSuggestion(state),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVar
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Stat card
// ---------------------------------------------------------------------------

@Composable
private fun StatCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    EmotionCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text  = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVar
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Empty state
// ---------------------------------------------------------------------------

@Composable
private fun EmptyProgressContent(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🌱", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(text = "Chưa có dữ liệu", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            text      = "Con hãy bắt đầu bài học đầu tiên nhé.",
            style     = MaterialTheme.typography.bodyLarge,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun accuracyLabel(rate: Float): String = when {
    rate >= 0.8f -> "Xuất sắc"
    rate >= 0.5f -> "Tiến bộ"
    else         -> "Cần luyện thêm"
}

private fun parentSuggestion(state: ProgressUiState): String {
    val mistake = state.mostMistakenEmotion
    return if (mistake != null) {
        "Con hay nhầm với cảm xúc \"${mistake.toLabel()}\". " +
        "Ba mẹ có thể thực hành nhận biết cảm xúc này cùng con qua các tình huống hằng ngày."
    } else if (state.completedLessons > 0) {
        "Con đã học ${state.completedLessons} bài. " +
        "Hãy khuyến khích con ghi nhật ký cảm xúc mỗi ngày để tạo thói quen."
    } else {
        "Hãy cùng con bắt đầu từ phần Học cảm xúc. Mỗi ngày 5 phút là đủ!"
    }
}

private fun EmotionType.toLabel(): String = when (this) {
    EmotionType.HAPPY     -> "Vui vẻ"
    EmotionType.SAD       -> "Buồn bã"
    EmotionType.ANGRY     -> "Tức giận"
    EmotionType.SURPRISED -> "Ngạc nhiên"
    EmotionType.CALM      -> "Bình tĩnh"
    EmotionType.TIRED     -> "Mệt mỏi"
}

private fun EmotionType.toEmoji(): String = when (this) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProgressScreenPreview() {
    EmotionFriendTheme {
        ProgressContent(
            state = ProgressUiState(
                isLoading            = false,
                completedLessons     = 12,
                accuracyRate         = 0.75f,
                mostMistakenEmotion  = EmotionType.ANGRY,
                journalCount         = 5,
                encouragementMessage = "Mình cùng luyện thêm một chút nhé. 💪"
            )
        )
    }
}
