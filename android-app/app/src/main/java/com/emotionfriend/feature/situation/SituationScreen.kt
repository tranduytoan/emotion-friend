package com.emotionfriend.feature.situation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionOptionButton
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.FeedbackBanner
import com.emotionfriend.core.designsystem.components.FeedbackType
import com.emotionfriend.core.designsystem.theme.EmotionAngry
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionTired
import com.emotionfriend.core.designsystem.theme.EmotionTiredBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSad
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionSurprised
import com.emotionfriend.core.designsystem.theme.EmotionSurprisedBg
import com.emotionfriend.core.designsystem.theme.FeedbackCorrectBg
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.domain.model.EmotionType

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun SituationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SituationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val topTitle = when {
        state.isLoading || state.totalQuestions == 0 -> "Tình huống xã hội"
        state.isSessionComplete                       -> "Hoàn thành!"
        else -> "Câu ${state.questionIndex + 1}/${state.totalQuestions}"
    }

    EmotionScreenScaffold(title = topTitle, onBack = onBack) {
        when {
            state.isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.isSessionComplete -> {
                SessionCompleteContent(
                    totalQuestions = state.totalQuestions,
                    onRestart      = viewModel::resetSession,
                    modifier       = modifier
                )
            }

            state.currentScenario == null -> {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "Không có tình huống nào.\nVui lòng thử lại sau.",
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val scenario = state.currentScenario!!
                ScenarioContent(
                    situationText     = scenario.situationText,
                    options           = scenario.options,
                    selectedEmotion   = state.selectedEmotion,
                    isAnswerSubmitted  = state.isAnswerSubmitted,
                    isCorrect         = state.isCorrect,
                    explanation       = state.explanation,
                    onSelectEmotion   = viewModel::selectEmotion,
                    onSubmit          = viewModel::submitAnswer,
                    onNext            = viewModel::nextScenario,
                    modifier          = modifier
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Scenario content
// ---------------------------------------------------------------------------

@Composable
private fun ScenarioContent(
    situationText: String,
    options: List<EmotionType>,
    selectedEmotion: EmotionType?,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean?,
    explanation: String,
    onSelectEmotion: (EmotionType) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- Story panel -------------------------------------------------------
        EmotionCard {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SkyBlueLight,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text     = "📖",
                    style    = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text      = situationText,
                    style     = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 36.dp, end = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text      = "Bạn trong câu chuyện cảm thấy thế nào?",
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        // --- Emotion options (2-column grid) -----------------------------------
        options.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                row.forEach { type ->
                    val visuals = type.toOptionVisuals()
                    EmotionOptionButton(
                        label          = visuals.label,
                        emoji          = visuals.emoji,
                        selected       = selectedEmotion == type,
                        containerColor = visuals.bg,
                        borderColor    = visuals.accent,
                        onClick        = { onSelectEmotion(type) },
                        modifier       = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // --- Feedback banner ---------------------------------------------------
        FeedbackBanner(
            visible = isAnswerSubmitted,
            type    = if (isCorrect == true) FeedbackType.CORRECT else FeedbackType.WRONG,
            message = if (isCorrect == true)
                "Chính xác! Con làm tốt lắm. 🌟"
            else
                "Không sao, mình thử lại nhé. 💪"
        )

        // --- Explanation card (visible after submission) -----------------------
        if (isAnswerSubmitted && explanation.isNotBlank()) {
            EmotionCard(
                modifier = Modifier.background(
                    color  = FeedbackCorrectBg,
                    shape  = MaterialTheme.shapes.large
                )
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "💡", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text     = explanation,
                        style    = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // --- Action button -----------------------------------------------------
        if (!isAnswerSubmitted) {
            EmotionPrimaryButton(
                text    = "Xác nhận",
                onClick = onSubmit,
                enabled = selectedEmotion != null
            )
        } else {
            EmotionPrimaryButton(
                text    = "Tình huống tiếp theo →",
                onClick = onNext
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Session complete
// ---------------------------------------------------------------------------

@Composable
private fun SessionCompleteContent(
    totalQuestions: Int,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🏆", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(text = "Xuất sắc!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text      = "Con đã xem qua $totalQuestions tình huống!",
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        EmotionPrimaryButton(text = "Chơi lại", onClick = onRestart)
    }
}

// ---------------------------------------------------------------------------
// EmotionType → visual mapping
// ---------------------------------------------------------------------------

private data class OptionVisuals(val label: String, val emoji: String, val accent: Color, val bg: Color)

private fun EmotionType.toOptionVisuals(): OptionVisuals = when (this) {
    EmotionType.HAPPY     -> OptionVisuals("Vui vẻ",     "😊", EmotionHappy,     EmotionHappyBg)
    EmotionType.SAD       -> OptionVisuals("Buồn bã",    "😢", EmotionSad,       EmotionSadBg)
    EmotionType.ANGRY     -> OptionVisuals("Tức giận",   "😠", EmotionAngry,     EmotionAngryBg)
    EmotionType.SURPRISED -> OptionVisuals("Ngạc nhiên", "😲", EmotionSurprised, EmotionSurprisedBg)
    EmotionType.CALM      -> OptionVisuals("Bình tĩnh",  "😌", EmotionCalm,      EmotionCalmBg)
    EmotionType.TIRED     -> OptionVisuals("Mệt mỏi",    "😴", EmotionTired,     EmotionTiredBg)
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SituationScreenPreview() {
    EmotionFriendTheme {
        ScenarioContent(
            situationText     = "Lan bị mất quả bóng bay. Lan cảm thấy thế nào?",
            options           = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.ANGRY, EmotionType.SURPRISED),
            selectedEmotion   = EmotionType.SAD,
            isAnswerSubmitted  = false,
            isCorrect         = null,
            explanation       = "",
            onSelectEmotion   = {},
            onSubmit          = {},
            onNext            = {}
        )
    }
}
