package com.emotionfriend.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.emotionfriend.core.designsystem.components.ProgressPill
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.theme.EmotionAngry
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSad
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionSurprised
import com.emotionfriend.core.designsystem.theme.EmotionSurprisedBg
import com.emotionfriend.core.designsystem.theme.EmotionTired
import com.emotionfriend.core.designsystem.theme.EmotionTiredBg
import com.emotionfriend.core.designsystem.theme.SkyBlue40
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

    when (state.phase) {
        LearnPhase.SETS_LIST -> SetsListScreen(
            sets      = state.lessonSets,
            isLoading = state.isLoading,
            tts       = tts,
            onBack    = onBack,
            onOpenSet = viewModel::openSet,
            onContinue = viewModel::openFirstIncompleteSet,
            modifier  = modifier,
        )

        LearnPhase.QUESTION -> {
            EmotionScreenScaffold(
                title  = state.lessonSets.find { it.id == state.activeSetId }?.title ?: "Học cảm xúc",
                onBack = viewModel::backToSetsList,
            ) {
                when {
                    state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    state.currentQuestion == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Không có câu hỏi.\nVui lòng thử lại.", textAlign = TextAlign.Center)
                    }
                    else -> QuestionContent(
                        question           = state.currentQuestion!!,
                        selectedEmotion    = state.selectedEmotion,
                        isAnswerSubmitted  = state.isAnswerSubmitted,
                        isCorrect          = state.isCorrect,
                        feedbackMessage    = state.feedbackMessage,
                        currentQuestion    = state.questionIndex + 1,
                        totalQuestions     = state.totalQuestionsInSet,
                        tts                = tts,
                        onSelectAnswer     = viewModel::selectAnswer,
                        onSubmit           = viewModel::submitAnswer,
                        onNext             = viewModel::nextQuestion,
                        modifier           = modifier,
                    )
                }
            }
        }

        LearnPhase.SET_COMPLETE -> SetCompleteContent(
            onContinue = viewModel::openFirstIncompleteSet,
            onBack     = viewModel::backToSetsList,
            modifier   = modifier,
        )
    }
}

// ---------------------------------------------------------------------------
// Sets list screen
// ---------------------------------------------------------------------------

@Composable
private fun SetsListScreen(
    sets: List<LessonSetInfo>,
    isLoading: Boolean,
    tts: TtsPlayer,
    onBack: () -> Unit,
    onOpenSet: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val welcomeMsg = "Chào mừng con đến trang Học cảm xúc! Con hãy chọn bộ bài mà con muốn học nhé."

    LaunchedEffect(Unit) {
        delay(400)
        tts.speak(welcomeMsg)
    }
    Scaffold(
        floatingActionButton = {
            if (!isLoading && sets.any { !it.isComplete }) {
                ExtendedFloatingActionButton(
                    onClick = onContinue,
                    icon    = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    text    = { Text("Học tiếp") },
                )
            }
        }
    ) { innerPadding ->
        EmotionScreenScaffold(title = "Học cảm xúc", onBack = onBack) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(
                    contentPadding    = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier          = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(sets, key = { it.id }) { set ->
                        LessonSetCard(set = set, onClick = { onOpenSet(set.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LessonSetCard(
    set: LessonSetInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeEmoji = "🏫"
    val statusEmoji = when {
        set.isComplete             -> "✅"
        set.correctCount > 0       -> "📖"
        else                       -> "🔒"
    }

    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = typeEmoji, style = MaterialTheme.typography.titleLarge)
                Text(
                    text     = set.title,
                    style    = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(text = statusEmoji, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text  = "${set.correctCount} / ${set.totalCount} câu đúng",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress         = { set.progressFraction },
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color            = if (set.isComplete) Color(0xFF4CAF50) else SkyBlue40,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Question content
// ---------------------------------------------------------------------------

@Composable
private fun QuestionContent(
    question: ActiveQuestion,
    selectedEmotion: EmotionType?,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean?,
    feedbackMessage: String,
    currentQuestion: Int,
    totalQuestions: Int,
    tts: TtsPlayer,
    onSelectAnswer: (EmotionType) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Auto-play the prompt when a new question loads
    LaunchedEffect(question.id) {
        delay(400)
        tts.speak(question.prompt)
    }

    // Read feedback aloud after submission
    LaunchedEffect(isAnswerSubmitted) {
        if (isAnswerSubmitted) {
            delay(300)
            if (isCorrect == true) tts.speak(FeedbackPhrases.randomCorrect())
            else tts.speak(FeedbackPhrases.randomIncorrect())
        }
    }

    // Wrong-answer red flash — auto-dismisses after 800 ms
    var showWrongFlash by remember(isAnswerSubmitted) { mutableStateOf(false) }
    LaunchedEffect(isAnswerSubmitted, isCorrect) {
        if (isAnswerSubmitted && isCorrect == false) {
            showWrongFlash = true
            delay(800)
            showWrongFlash = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Teacher Vy companion — audio only
            TeacherMyGuide(
                message = question.prompt,
                onSpeak = { tts.speak(question.prompt) },
            )

            // Progress pill (no mode toggle)
            ProgressPill(
                current  = currentQuestion,
                total    = totalQuestions,
                modifier = Modifier.fillMaxWidth(),
            )

            // Scenario title (if any)
            if (question.subtitle.isNotEmpty()) {
                Text(
                    text     = question.subtitle,
                    style    = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color    = SkyBlue40,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            // Audio card — tap to replay
            EmotionCard {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "🔊", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "Bạn này đang cảm thấy gì?",
                        style     = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { tts.speak(question.prompt) }) {
                        Text("🔊 Nghe lại")
                    }
                }
            }

            // Emoji options grid (2 columns)
            question.options.chunked(2).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier              = Modifier.fillMaxWidth(),
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
                            modifier       = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // Wrong answer message (replaces FeedbackBanner for wrong)
            AnimatedVisibility(
                visible = isAnswerSubmitted && isCorrect == false,
                enter   = fadeIn(),
                exit    = fadeOut(),
            ) {
                Text(
                    text      = feedbackMessage,
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }

            if (!isAnswerSubmitted) {
                EmotionPrimaryButton(
                    text    = "Xác nhận",
                    onClick = onSubmit,
                    enabled = selectedEmotion != null,
                )
            } else {
                EmotionPrimaryButton(
                    text    = "Câu tiếp theo →",
                    onClick = onNext,
                )
            }
        }

        // Confetti for correct answers
        ConfettiOverlay(
            active   = isAnswerSubmitted && isCorrect == true,
            modifier = Modifier.fillMaxSize(),
        )

        // Red flash overlay for wrong answers
        AnimatedVisibility(
            visible  = showWrongFlash,
            enter    = fadeIn(tween(80)),
            exit     = fadeOut(tween(600)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.25f))
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Set complete
// ---------------------------------------------------------------------------

@Composable
private fun SetCompleteContent(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmotionScreenScaffold(title = "Hoàn thành bộ!", onBack = onBack) {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))
            Text(
                text      = "✅ Xong bộ này rồi!",
                style     = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            EmotionPrimaryButton(text = "Học tiếp bộ tiếp theo →", onClick = onContinue)
            TextButton(onClick = onBack) { Text("Quay lại danh sách") }
        }
    }
}

// ---------------------------------------------------------------------------
// EmotionType → visual mapping
// ---------------------------------------------------------------------------

private data class OptionVisuals(
    val label: String,
    val emoji: String,
    val accent: Color,
    val bg: Color,
)

private fun EmotionType.toOptionVisuals(): OptionVisuals = when (this) {
    EmotionType.HAPPY     -> OptionVisuals("Vui vẻ",     "😊", EmotionHappy,     EmotionHappyBg)
    EmotionType.SAD       -> OptionVisuals("Buồn bã",    "😢", EmotionSad,       EmotionSadBg)
    EmotionType.ANGRY     -> OptionVisuals("Tức giận",   "😠", EmotionAngry,     EmotionAngryBg)
    EmotionType.SURPRISED -> OptionVisuals("Ngạc nhiên", "😲", EmotionSurprised, EmotionSurprisedBg)
    EmotionType.CALM      -> OptionVisuals("Bình tĩnh",  "😌", EmotionCalm,      EmotionCalmBg)
    EmotionType.TIRED     -> OptionVisuals("Mệt mỏi",    "😴", EmotionTired,     EmotionTiredBg)
}

