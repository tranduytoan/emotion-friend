package com.emotionfriend.feature.journal

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
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
import kotlinx.coroutines.launch
import java.io.File

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun DailyCheckInScreen(
    onComplete: () -> Unit,
    viewModel: DailyCheckInViewModel = hiltViewModel(),
) {
    val state   by viewModel.uiState.collectAsState()
    val tts     = rememberTtsPlayer()
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // Navigate away (skip or after save)
    LaunchedEffect(state.shouldNavigateToHome) {
        if (state.shouldNavigateToHome) onComplete()
    }

    // Play greeting once the screen is ready to show
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.canCheckIn && !state.alreadySavedToday) {
            delay(600)
            tts.speak("Cảm xúc của con ngày hôm nay là gì?")
        }
    }

    // Don't render UI while loading or redirecting
    if (state.isLoading || state.shouldNavigateToHome) return

    // ── Recording state ──────────────────────────────────────────────────────

    var isRecording   by remember { mutableStateOf(false) }
    var secondsLeft   by remember { mutableIntStateOf(10) }
    var hasRecording  by remember { mutableStateOf(false) }
    val recorderRef = remember { arrayOfNulls<MediaRecorder>(1) }

    // Audio permission
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasAudioPermission = granted }

    // Release recorder when composable leaves
    DisposableEffect(Unit) {
        onDispose {
            recorderRef[0]?.apply { runCatching { stop() }; release() }
            recorderRef[0] = null
        }
    }

    fun startRecording() {
        val file = File(context.cacheDir, "checkin_${System.currentTimeMillis()}.3gp")
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorderRef[0] = recorder
        isRecording = true
        secondsLeft = 10
        scope.launch {
            repeat(10) {
                delay(1_000)
                secondsLeft--
            }
            recorderRef[0]?.apply { runCatching { stop() }; release() }
            recorderRef[0] = null
            isRecording  = false
            hasRecording = true
            viewModel.setAudioPath(file.absolutePath)
        }
    }

    fun stopRecording() {
        recorderRef[0]?.apply { runCatching { stop() }; release() }
        recorderRef[0] = null
        isRecording = false
        hasRecording = true
    }

    // ── Mic button scale animation ───────────────────────────────────────────

    val micScale by animateFloatAsState(
        targetValue   = if (isRecording) 1.15f else 1f,
        animationSpec = tween(300),
        label         = "mic_scale",
    )

    // ── UI ───────────────────────────────────────────────────────────────────

    EmotionScreenScaffold {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Title ─────────────────────────────────────────────────────
            Text(
                text      = "Cảm xúc của con\nngày hôm nay? 💭",
                style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )

            // Replay TTS
            TextButton(onClick = { tts.speak("Cảm xúc của con ngày hôm nay là gì?") }) {
                Text("🔊 Nghe lại câu hỏi", style = MaterialTheme.typography.bodyMedium)
            }

            // ── Emotion grid — emoji-only, 3 columns ──────────────────────
            val emotions = EmotionType.entries
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                emotions.chunked(3).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier              = Modifier.fillMaxWidth(),
                    ) {
                        row.forEach { type ->
                            val visuals  = type.toCheckInVisuals()
                            val selected = state.selectedEmotion == type
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier         = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(
                                        if (selected) visuals.bg
                                        else visuals.bg.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = if (selected) 3.dp else 1.5.dp,
                                        color = if (selected) visuals.accent
                                                else visuals.accent.copy(alpha = 0.35f),
                                        shape = MaterialTheme.shapes.large,
                                    )
                                    .clickable(role = Role.Button) {
                                        viewModel.selectEmotion(type)
                                        tts.speak(visuals.label)
                                    }
                                    .padding(8.dp),
                            ) {
                                Text(text = visuals.emoji, fontSize = 48.sp)
                            }
                        }
                        // Pad incomplete rows so columns stay aligned
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            // ── Voice record button ───────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .scale(micScale)
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) EmotionAngry
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                        .clickable(role = Role.Button) {
                            if (isRecording) {
                                stopRecording()
                            } else if (hasAudioPermission) {
                                startRecording()
                            } else {
                                permLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        .padding(8.dp),
                ) {
                    Text(text = if (isRecording) "⏹️" else "🎙️", fontSize = 34.sp)
                }
                Text(
                    text = when {
                        isRecording  -> "Đang ghi âm... còn $secondsLeft giây"
                        hasRecording -> "✅ Đã ghi âm xong"
                        else         -> "Nói cảm xúc của con (10 giây)"
                    },
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Save & skip buttons ───────────────────────────────────────
            EmotionPrimaryButton(
                text    = "Lưu cảm xúc hôm nay",
                onClick = viewModel::saveCheckIn,
                enabled = state.selectedEmotion != null && !isRecording,
            )

            TextButton(onClick = viewModel::skipCheckIn) {
                Text(
                    text  = "Bỏ qua",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// EmotionType → visual mapping (local to this screen)
// ---------------------------------------------------------------------------

private data class CheckInVisuals(
    val label: String,
    val emoji: String,
    val accent: Color,
    val bg: Color,
)

private fun EmotionType.toCheckInVisuals(): CheckInVisuals = when (this) {
    EmotionType.HAPPY     -> CheckInVisuals("Vui vẻ",     "😊", EmotionHappy,     EmotionHappyBg)
    EmotionType.SAD       -> CheckInVisuals("Buồn bã",    "😢", EmotionSad,       EmotionSadBg)
    EmotionType.ANGRY     -> CheckInVisuals("Tức giận",   "😠", EmotionAngry,     EmotionAngryBg)
    EmotionType.SURPRISED -> CheckInVisuals("Ngạc nhiên", "😲", EmotionSurprised, EmotionSurprisedBg)
    EmotionType.CALM      -> CheckInVisuals("Bình tĩnh",  "😌", EmotionCalm,      EmotionCalmBg)
    EmotionType.TIRED     -> CheckInVisuals("Mệt mỏi",    "😴", EmotionTired,     EmotionTiredBg)
}
