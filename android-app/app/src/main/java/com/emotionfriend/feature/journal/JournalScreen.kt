package com.emotionfriend.feature.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
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
    val state by viewModel.uiState.collectAsState()

    // Auto-clear the confirmation banner after 2 seconds
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            delay(2_000)
            viewModel.clearFeedback()
        }
    }

    EmotionScreenScaffold(title = "Cảm xúc hôm nay", onBack = onBack) {
        JournalContent(
            emotionOptions  = state.emotionOptions,
            selectedEmotion = state.selectedEmotion,
            saveSuccess     = state.saveSuccess,
            recentEntries   = state.recentEntries,
            onSelectEmotion = viewModel::selectEmotion,
            onSave          = viewModel::saveCurrentEmotion,
            modifier        = modifier
        )
    }
}

// ---------------------------------------------------------------------------
// Main content
// ---------------------------------------------------------------------------

@Composable
private fun JournalContent(
    emotionOptions: List<EmotionType>,
    selectedEmotion: EmotionType?,
    saveSuccess: Boolean,
    recentEntries: List<JournalEntry>,
    onSelectEmotion: (EmotionType) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- Header -----------------------------------------------------------
        Text(
            text      = "Hôm nay con thấy thế nào? 💭",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        // --- Emotion grid (2 columns) -----------------------------------------
        emotionOptions.chunked(2).forEach { row ->
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

        // --- Confirmation banner -----------------------------------------------
        AnimatedVisibility(
            visible = saveSuccess,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) {
            FeedbackBanner(
                visible = saveSuccess,
                type    = FeedbackType.CORRECT,
                message = "Đã lưu cảm xúc của con. 💙"
            )
        }

        // --- Save button ------------------------------------------------------
        EmotionPrimaryButton(
            text    = "Lưu cảm xúc",
            onClick = onSave,
            enabled = selectedEmotion != null
        )

        // --- Recent entries ---------------------------------------------------
        if (recentEntries.isNotEmpty()) {
            HorizontalDivider()
            Text(
                text  = "Gần đây",
                style = MaterialTheme.typography.titleMedium
            )
            recentEntries.forEach { entry ->
                RecentEntryRow(entry = entry)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Recent entry row
// ---------------------------------------------------------------------------

@Composable
private fun RecentEntryRow(entry: JournalEntry) {
    val visuals   = entry.emotionType.toOptionVisuals()
    val formatter = SimpleDateFormat("dd/MM  HH:mm", Locale.getDefault())
    val dateStr   = formatter.format(Date(entry.createdAt))

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = visuals.emoji, style = MaterialTheme.typography.titleLarge)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = visuals.label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text  = dateStr,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVar
            )
        }
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
private fun JournalScreenPreview() {
    EmotionFriendTheme {
        JournalContent(
            emotionOptions  = EmotionType.entries,
            selectedEmotion = EmotionType.HAPPY,
            saveSuccess     = false,
            recentEntries   = emptyList(),
            onSelectEmotion = {},
            onSave          = {}
        )
    }
}
