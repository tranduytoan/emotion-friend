package com.emotionfriend.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.TtsPlayer
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionOptionButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.components.VyEmotion
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
import com.emotionfriend.domain.model.EmotionType
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Data model (private, UI-only)
// ---------------------------------------------------------------------------

private data class HomeActivity(
    val emoji: String,
    val label: String,
    val background: Color,
    val onClick: () -> Unit,
)

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToSituation: () -> Unit = {},
    onNavigateToExpress: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStory: () -> Unit = {},
    onNavigateToConfide: () -> Unit = {},
    userName: String = "",
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    BackHandler {} // Prevent back to login

    val state   by viewModel.uiState.collectAsState()
    val tts     = rememberTtsPlayer()
    val context = LocalContext.current

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // If permission just granted, start the mic recording
        if (granted) viewModel.startRecording(context)
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    AnimatedContent(
        targetState    = state.checkInPhase,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label          = "homePhase",
    ) { phase ->
        when (phase) {
            CheckInPhase.WELCOME -> WelcomePhase(
                isFirstVisitToday = state.isFirstVisitToday,
                tts               = tts,
                onFinished        = viewModel::onWelcomeFinished,
                onSkip            = viewModel::skipCheckIn,
                modifier          = modifier,
            )
            CheckInPhase.SELECT_EMOTION -> SelectEmotionPhase(
                tts      = tts,
                onSelect = { emotion ->
                    viewModel.selectEmotion(emotion)
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        viewModel.startRecording(context)
                    } else {
                        permLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onSkip   = viewModel::skipCheckIn,
                modifier = modifier,
            )
            CheckInPhase.RECORDING -> RecordingPhase(
                secondsLeft = state.recordingSecondsLeft,
                onTick      = viewModel::onRecordingTick,
                tts         = tts,
                modifier    = modifier,
            )
            CheckInPhase.DONE -> DashboardPhase(
                onNavigateToLearn    = onNavigateToLearn,
                onNavigateToExpress  = onNavigateToExpress,
                onNavigateToRelax    = onNavigateToRelax,
                onNavigateToJournal  = onNavigateToJournal,
                onNavigateToProgress = onNavigateToProgress,
                onNavigateToProfile  = onNavigateToProfile,
                onNavigateToStory    = onNavigateToStory,
                onNavigateToConfide  = onNavigateToConfide,
                modifier             = modifier,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Phase 1 — Welcome greeting TTS, auto-advances
// ---------------------------------------------------------------------------

@Composable
private fun WelcomePhase(
    isFirstVisitToday: Boolean,
    tts: TtsPlayer,
    onFinished: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val greeting = if (isFirstVisitToday)
        "Cô giáo Vy đây, chào mừng con trở lại lớp học Cảm xúc. Hôm nay con cảm thấy thế nào?"
    else
        "Cô giáo Vy đây, chào mừng con trở lại lớp học Cảm xúc. Hôm nay con tiếp tục học cảm xúc cùng cô Vy nhé."

    LaunchedEffect(Unit) {
        delay(400)
        tts.speak(greeting)
        delay(4_500)
        onFinished()
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier            = Modifier.padding(32.dp),
        ) {
            TeacherMyGuide(
                message   = greeting,
                onSpeak   = { tts.speak(greeting) },
                vyEmotion = VyEmotion.EXCITED,
            )
            TextButton(onClick = onSkip) {
                Text("Bỏ qua →", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Phase 2 — Emotion selection grid
// ---------------------------------------------------------------------------

@Composable
private fun SelectEmotionPhase(
    tts: TtsPlayer,
    onSelect: (EmotionType) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prompt = "Hôm nay con cảm thấy thế nào?"

    LaunchedEffect(Unit) {
        delay(300)
        tts.speak(prompt)
    }

    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        TeacherMyGuide(
            message   = prompt,
            onSpeak   = { tts.speak(prompt) },
            vyEmotion = VyEmotion.EXCITED,
        )
        Text(
            text      = "Con đang cảm thấy thế nào? 💭",
            style     = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        val emotions = EmotionType.entries
        emotions.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { type ->
                    val v = type.toHomeVisuals()
                    EmotionOptionButton(
                        label          = v.label,
                        emoji          = v.emoji,
                        selected       = false,
                        containerColor = v.bg,
                        borderColor    = v.accent,
                        showLabel      = true,
                        onClick        = { tts.speak(v.label); onSelect(type) },
                        modifier       = Modifier.weight(1f),
                    )
                }
                if (rowItems.size < 2) repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        TextButton(onClick = onSkip) {
            Text("Bỏ qua", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ---------------------------------------------------------------------------
// Phase 3 — 5-second recording countdown
// ---------------------------------------------------------------------------

@Composable
private fun RecordingPhase(
    secondsLeft: Int,
    onTick: () -> Unit,
    tts: TtsPlayer,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1_000)
            onTick()
        }
    }
    LaunchedEffect(secondsLeft) {
        if (secondsLeft == 0) {
            delay(400)
            tts.speak("Được rồi! Cô đã nghe cảm xúc của con. Hôm nay tiếp tục học cảm xúc cùng cô Vy nhé.")
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier            = Modifier.padding(32.dp),
        ) {
            TeacherMyGuide(
                message   = "Con nói cảm xúc của con nhé!",
                vyEmotion = VyEmotion.HAPPY,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(EmotionHappyBg),
            ) {
                Text(text = "🎙️", fontSize = 56.sp)
            }
            Text(
                text      = if (secondsLeft > 0) "⏱ $secondsLeft giây..." else "✅ Ghi âm xong!",
                style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color     = if (secondsLeft > 0) EmotionHappy else EmotionCalm,
            )
            Text(
                text      = "Con hãy nói cảm xúc của mình nhé 🎙️",
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Phase 4 — Main activity grid
// ---------------------------------------------------------------------------

@Composable
private fun DashboardPhase(
    onNavigateToLearn: () -> Unit,
    onNavigateToExpress: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStory: () -> Unit = {},
    onNavigateToConfide: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val activities = listOf(
        HomeActivity("📚", "Học cảm xúc",     EmotionHappyBg, onNavigateToLearn),
        HomeActivity("📖", "Kể chuyện",        EmotionSadBg,   onNavigateToStory),
        HomeActivity("💬", "Tâm sự",           EmotionSurprisedBg, onNavigateToConfide),
        HomeActivity("📸", "Ghi lại",         EmotionCalmBg,  onNavigateToExpress),
        HomeActivity("🌈", "Thư giãn",         EmotionAngryBg, onNavigateToRelax),
    )

    val tts = rememberTtsPlayer()
    val welcomeMsg = "Chào mừng con trở lại! Hôm nay mình cùng khám phá cảm xúc nhé!"

    // Greeting when dashboard first appears
    LaunchedEffect(Unit) {
        delay(400)
        tts.speak("Chào mừng con trở lại lớp học cảm xúc cùng cô Vy nhé!")
    }

    EmotionScreenScaffold {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TeacherMyGuide(
                message   = welcomeMsg,
                onSpeak   = { tts.speak(welcomeMsg) },
                vyEmotion = VyEmotion.HAPPY,
                modifier  = Modifier.padding(horizontal = 24.dp),
            )
            
            Text(
                text = "Hôm nay con muốn làm gì? ✨",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(activities) { activity ->
                    ActivityCard(
                        activity = activity,
                        onSpeak = { tts.speak(activity.label) }
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Activity card shown in the grid
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityCard(
    activity: HomeActivity,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onSpeak(); activity.onClick() },
        colors = CardDefaults.cardColors(containerColor = activity.background),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = activity.emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = activity.label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Emotion visuals helper
// ---------------------------------------------------------------------------

private data class HomeEmotionVisuals(val label: String, val emoji: String, val bg: Color, val accent: Color)

private fun EmotionType.toHomeVisuals(): HomeEmotionVisuals = when (this) {
    EmotionType.HAPPY     -> HomeEmotionVisuals("Vui",        "😄", EmotionHappyBg,     EmotionHappy)
    EmotionType.SAD       -> HomeEmotionVisuals("Buồn",       "😢", EmotionSadBg,       EmotionSad)
    EmotionType.ANGRY     -> HomeEmotionVisuals("Tức giận",   "😠", EmotionAngryBg,     EmotionAngry)
    EmotionType.SURPRISED -> HomeEmotionVisuals("Ngạc nhiên", "😲", EmotionSurprisedBg, EmotionSurprised)
    EmotionType.CALM      -> HomeEmotionVisuals("Bình tĩnh",  "😌", EmotionCalmBg,      EmotionCalm)
    EmotionType.TIRED     -> HomeEmotionVisuals("Mệt mỏi",   "😴", EmotionTiredBg,     EmotionTired)
}
