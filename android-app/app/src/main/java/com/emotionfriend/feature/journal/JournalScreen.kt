package com.emotionfriend.feature.journal

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionOptionButton
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.components.TeacherMyMessages
import com.emotionfriend.core.designsystem.components.VyEmotion
import com.emotionfriend.core.designsystem.components.toVyEmotionForCompanion
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
import com.emotionfriend.domain.model.JournalEntry
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun JournalScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state   = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    val audioPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startRecording(context)
    }

    AnimatedContent(
        targetState = state.phase,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label       = "journal_phase",
    ) { phase ->
        when (phase) {
            JournalPhase.HISTORY -> HistoryScreen(
                entries       = state.allEntries,
                isLoading     = state.isLoading,
                playingId     = state.playingEntryId,
                onBack        = onBack,
                onAdd         = viewModel::startAddEntry,
                onTogglePlay  = viewModel::togglePlayback,
                modifier      = modifier,
            )

            JournalPhase.SELECT_EMOTION -> SelectEmotionPhase(
                selectedEmotion = state.selectedEmotion,
                onSelect        = viewModel::selectEmotion,
                onConfirm       = {
                    audioPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onCancel        = viewModel::cancelAddEntry,
                modifier        = modifier,
            )

            JournalPhase.RECORDING -> RecordingPhase(
                secondsLeft = state.recordingSecondsLeft,
                onTick      = viewModel::onRecordingTick,
                onStop      = viewModel::stopRecording,
                modifier    = modifier,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// History list
// ---------------------------------------------------------------------------

@Composable
private fun HistoryScreen(
    entries: List<JournalEntry>,
    isLoading: Boolean,
    playingId: String?,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onTogglePlay: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tts = rememberTtsPlayer()
    val welcomeMsg = "Cảm xúc của con hôm nay như thế nào? Bấm nút cộng để thêm cảm xúc mới nhé!"
    val latestEmotion = entries.firstOrNull()?.emotionType
    val supportMsg = remember(latestEmotion) {
        TeacherMyMessages.journalSupport(latestEmotion)
    }
    val supportEmotion = latestEmotion?.toVyEmotionForCompanion() ?: VyEmotion.CALM

    LaunchedEffect(Unit) {
        delay(400)
        tts.speak(welcomeMsg)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Thêm cảm xúc")
            }
        }
    ) { innerPadding ->
        EmotionScreenScaffold(title = "Cảm xúc của con 📓", onBack = onBack) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                entries.isEmpty() -> Column(
                    modifier            = Modifier.fillMaxSize().padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TeacherMyGuide(
                        message   = welcomeMsg,
                        onSpeak   = { tts.speak(welcomeMsg) },
                        vyEmotion = VyEmotion.HAPPY,
                    )
                    Text("📭", style = MaterialTheme.typography.displayLarge)
                    Text(
                        text      = "Chưa có cảm xúc nào\nBấm + để thêm",
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                else -> LazyColumn(
                    contentPadding      = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                ) {
                    item {
                        TeacherMyGuide(
                            message   = supportMsg,
                            onSpeak   = { tts.speak(supportMsg) },
                            vyEmotion = supportEmotion,
                            modifier  = Modifier.padding(vertical = 12.dp),
                        )
                    }
                    items(entries, key = { it.id }) { entry ->
                        EntryRow(
                            entry        = entry,
                            isPlaying    = playingId == entry.id,
                            onTogglePlay = { onTogglePlay(entry) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryRow(
    entry: JournalEntry,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
) {
    val visuals   = entry.emotionType.toOptionVisuals()
    val formatter = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    val dateStr   = formatter.format(Date(entry.createdAt))

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = visuals.emoji, style = MaterialTheme.typography.titleLarge)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = visuals.label,
                style      = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = dateStr, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (entry.audioPath != null) {
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector        = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Dừng" else "Nghe lại",
                    tint               = visuals.accent,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Select emotion phase
// ---------------------------------------------------------------------------

@Composable
private fun SelectEmotionPhase(
    selectedEmotion: EmotionType?,
    onSelect: (EmotionType) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tts = rememberTtsPlayer()
    val prompt = "Hôm nay con thấy thế nào? Hãy chọn cảm xúc của con nhé!"
    val supportEmotion = selectedEmotion?.toVyEmotionForCompanion() ?: VyEmotion.EXCITED
    val supportMsg = remember(selectedEmotion) {
        TeacherMyMessages.journalSupport(selectedEmotion)
    }

    LaunchedEffect(Unit) {
        delay(300)
        tts.speak(prompt)
    }

    EmotionScreenScaffold(title = "Chọn cảm xúc", onBack = onCancel) {
        Column(
            modifier            = modifier.fillMaxSize().padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TeacherMyGuide(
                message   = supportMsg,
                onSpeak   = { tts.speak(supportMsg) },
                vyEmotion = supportEmotion,
            )
            Text(
                text      = "Hôm nay con thấy thế nào? 💭",
                style     = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )

            EmotionType.entries.chunked(2).forEach { row ->
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
                            onClick        = { onSelect(type) },
                            modifier       = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            EmotionPrimaryButton(
                text    = "Ghi âm cảm xúc 🎙️",
                onClick = onConfirm,
                enabled = selectedEmotion != null,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Recording phase (5 second countdown)
// ---------------------------------------------------------------------------

@Composable
private fun RecordingPhase(
    secondsLeft: Int,
    onTick: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1_000)
            onTick()
        }
    }
    // NOTE: No TTS here — anything spoken would be captured in the recording.

    EmotionScreenScaffold(title = "Đang ghi âm") {
        Column(
            modifier            = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🎙️", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text(
                text  = "Hãy nói cảm xúc của con...",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text  = "$secondsLeft",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
            )
            Spacer(Modifier.height(32.dp))
            IconButton(onClick = onStop) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dừng ghi âm",
                    modifier           = Modifier.size(48.dp),
                )
            }
            Text(
                text  = "Bấm để dừng sớm",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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


