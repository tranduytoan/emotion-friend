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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.FeedbackPhrases
import com.emotionfriend.core.audio.TtsPlayer
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.ConfettiOverlay
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionOptionButton
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.FeedbackBanner
import com.emotionfriend.core.designsystem.components.FeedbackType
import com.emotionfriend.core.designsystem.components.ProgressPill
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.components.TeacherMyMessages
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
import kotlinx.coroutines.delay

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
    val tts   = rememberTtsPlayer()

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
                    cardId                = card.id,
                    cardDescription       = card.description,
                    options               = state.options,
                    selectedEmotion       = state.selectedEmotion,
                    isAnswerSubmitted      = state.isAnswerSubmitted,
                    isCorrect             = state.isCorrect,
                    feedbackMessage       = state.feedbackMessage,
                    currentQuestion       = state.questionIndex + 1,
                    totalQuestions        = state.totalQuestions,
                    isChallengeMode       = state.isChallengeMode,
                    tts                   = tts,
                    onSelectAnswer        = viewModel::selectAnswer,
                    onSubmit              = viewModel::submitAnswer,
                    onNext                = viewModel::nextQuestion,
                    onToggleChallengeMode = viewModel::toggleChallengeMode,
                    modifier              = modifier
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Question content — audio-first: TTS replaces emoji flashcard
// ---------------------------------------------------------------------------

@Composable
private fun QuestionContent(
    cardId: String,
    cardDescription: String,
    options: List<EmotionType>,
    selectedEmotion: EmotionType?,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean?,
    feedbackMessage: String,
    currentQuestion: Int,
    totalQuestions: Int,
    isChallengeMode: Boolean,
    tts: TtsPlayer,
    onSelectAnswer: (EmotionType) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onToggleChallengeMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-play the description when a new card loads
    LaunchedEffect(cardId) {
        delay(400)
        tts.speak("$cardDescription. Bạn này đang cảm thấy gì?")
    }

    // Read diverse feedback aloud after submission
    LaunchedEffect(isAnswerSubmitted) {
        if (isAnswerSubmitted) {
            delay(300)
            if (isCorrect == true)
                tts.speak(FeedbackPhrases.randomCorrect())
            else
                tts.speak(FeedbackPhrases.randomIncorrect())
        }
    }

    // Companion message changes based on answer state
    val teacherMessage = remember(isAnswerSubmitted, isCorrect) {
        when {
            !isAnswerSubmitted -> TeacherMyMessages.randomLearn()
            isCorrect == true  -> TeacherMyMessages.randomCorrect()
            else               -> TeacherMyMessages.randomWrong()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // --- Teacher My companion ----------------------------------------
            TeacherMyGuide(
                message = teacherMessage,
                onSpeak = { tts.speak(teacherMessage) },
            )

            // --- Challenge mode toggle + Progress pill row -------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                ProgressPill(
                    current  = currentQuestion,
                    total    = totalQuestions,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onToggleChallengeMode) {
                    Text(
                        text  = if (isChallengeMode) "⚡ Thử thách" else "📖 Thường",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // --- Audio player card (replaces emoji flashcard) ----------------
            EmotionCard {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text  = "🔊",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "Bạn này đang cảm thấy gì?",
                        style     = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { tts.speak("$cardDescription. Bạn này đang cảm thấy gì?") }
                    ) {
                        Text("🔊 Nghe lại")
                    }
                }
            }

            // --- Options grid — emoji only, TTS on tap -----------------------
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
                            showLabel      = false,
                            onClick        = {
                                onSelectAnswer(type)
                                tts.speak(visuals.label)
                            },
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

        // --- Confetti burst on correct answer --------------------------------
        ConfettiOverlay(
            active   = isAnswerSubmitted && isCorrect == true,
            modifier = Modifier.fillMaxSize()
        )
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
