package com.emotionfriend.feature.learn

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
import com.emotionfriend.core.designsystem.components.ProgressPill
import com.emotionfriend.core.designsystem.theme.EmotionAngry
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSad
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionSurprised
import com.emotionfriend.core.designsystem.theme.EmotionSurprisedBg
import com.emotionfriend.core.designsystem.theme.EmotionTired
import com.emotionfriend.core.designsystem.theme.EmotionTiredBg
import com.emotionfriend.domain.model.EmotionType

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun LearnScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LearnEmotionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    EmotionScreenScaffold(title = "Học cảm xúc", onBack = onBack) {
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

            state.currentCard == null -> {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "Không có dữ liệu.\nVui lòng thử lại sau.",
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val card = state.currentCard!!
                QuestionContent(
                    emoji             = card.emoji,
                    options           = state.options,
                    selectedEmotion   = state.selectedEmotion,
                    isAnswerSubmitted  = state.isAnswerSubmitted,
                    isCorrect         = state.isCorrect,
                    feedbackMessage   = state.feedbackMessage,
                    currentQuestion   = state.questionIndex + 1,
                    totalQuestions    = state.totalQuestions,
                    onSelectAnswer    = viewModel::selectAnswer,
                    onSubmit          = viewModel::submitAnswer,
                    onNext            = viewModel::nextQuestion,
                    modifier          = modifier
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Question content
// ---------------------------------------------------------------------------

@Composable
private fun QuestionContent(
    emoji: String,
    options: List<EmotionType>,
    selectedEmotion: EmotionType?,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean?,
    feedbackMessage: String,
    currentQuestion: Int,
    totalQuestions: Int,
    onSelectAnswer: (EmotionType) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // --- Progress pill ---------------------------------------------------
        ProgressPill(
            current  = currentQuestion,
            total    = totalQuestions
        )

        // --- Flashcard -------------------------------------------------------
        EmotionCard {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text     = emoji,
                    style    = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text      = "Bạn này đang cảm thấy gì?",
                    style     = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        // --- Options grid (2 columns) -----------------------------------------
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
                        onClick        = { onSelectAnswer(type) },
                        modifier       = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // --- Feedback --------------------------------------------------------
        FeedbackBanner(
            visible = isAnswerSubmitted,
            type    = if (isCorrect == true) FeedbackType.CORRECT else FeedbackType.WRONG,
            message = feedbackMessage
        )

        // --- Action button ---------------------------------------------------
        if (!isAnswerSubmitted) {
            EmotionPrimaryButton(
                text    = "Xác nhận",
                onClick = onSubmit,
                enabled = selectedEmotion != null
            )
        } else {
            EmotionPrimaryButton(
                text    = "Câu tiếp theo →",
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
        Text(text = "🎉", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(text = "Tuyệt vời!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text      = "Con đã học xong $totalQuestions thẻ cảm xúc!",
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

private data class OptionVisuals(
    val label: String,
    val emoji: String,
    val accent: Color,
    val bg: Color
)

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
private fun LearnScreenPreview() {
    EmotionFriendTheme {
        QuestionContent(
            emoji             = "😊",
            options           = listOf(EmotionType.HAPPY, EmotionType.SAD, EmotionType.ANGRY, EmotionType.CALM),
            selectedEmotion   = EmotionType.HAPPY,
            isAnswerSubmitted  = false,
            isCorrect         = null,
            feedbackMessage   = "",
            currentQuestion   = 2,
            totalQuestions    = 5,
            onSelectAnswer    = {},
            onSubmit          = {},
            onNext            = {}
        )
    }
}
