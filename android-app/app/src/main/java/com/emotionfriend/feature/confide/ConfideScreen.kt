package com.emotionfriend.feature.confide

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.TeacherMyAvatar
import com.emotionfriend.core.designsystem.components.VyEmotion
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.OnSurface
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlue80
import com.emotionfriend.core.designsystem.theme.SurfaceVariant
import kotlinx.coroutines.delay

@Composable
fun ConfideScreen(
    onNavigateBack    : () -> Unit,
    onNavigateToStory : () -> Unit = {},
    onNavigateToRelax : () -> Unit = {},
    viewModel         : ConfideViewModel = hiltViewModel(),
) {
    val uiState   by viewModel.uiState.collectAsState()
    val tts        = rememberTtsPlayer()
    val listState  = rememberLazyListState()
    val context    = LocalContext.current

    // ─── STT setup ──────────────────────────────────────────────────────────

    var isListening by remember { mutableStateOf(false) }

    val recognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: return
                viewModel.sendMessage(text)
            }

            override fun onPartialResults(partial: Bundle?) {}

            override fun onError(error: Int) {
                isListening = false
                if (error != SpeechRecognizer.ERROR_NO_MATCH &&
                    error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT
                ) {
                    Toast.makeText(context, "Cô Vy không nghe rõ, con thử lại nhé 🎤", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        recognizer.setRecognitionListener(listener)
        onDispose { recognizer.destroy() }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Thiết bị chưa hỗ trợ nhận giọng nói", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
        }
        recognizer.startListening(intent)
        isListening = true
    }

    fun stopListening() {
        recognizer.stopListening()
        isListening = false
    }

    // ─── Auto-scroll ────────────────────────────────────────────────────────

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // ─── TTS: auto-play new AI replies ──────────────────────────────────────

    LaunchedEffect(uiState.messages) {
        val last = uiState.messages.lastOrNull()
        if (last != null && last.role == MessageRole.ASSISTANT && !last.isError) {
            delay(300)
            tts.speak(last.text)
        }
    }

    // ─── Greeting on entry ───────────────────────────────────────────────────

    LaunchedEffect(Unit) {
        delay(500)
        tts.speak("Chào con! Cô Vy đang lắng nghe. Con muốn kể điều gì không?")
    }

    // ─── UI ──────────────────────────────────────────────────────────────────

    EmotionScreenScaffold(
        title  = "Tâm sự cùng cô Vy",
        onBack = onNavigateBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            // Empty state
            if (uiState.messages.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TeacherMyAvatar(emotion = VyEmotion.HAPPY, size = 80)
                        Text(
                            text      = "Cô Vy đang lắng nghe con 💛",
                            style     = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text      = "Con có thể kể điều gì khiến con vui, buồn, hay lo lắng nhé!\n" +
                                        "Nhấn 🎤 để nói hoặc gõ vào ô bên dưới.",
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = OnSurfaceVar,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                // Chat messages
                LazyColumn(
                    state               = listState,
                    contentPadding      = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier            = Modifier.weight(1f),
                ) {
                    items(uiState.messages) { msg ->
                        ChatBubble(
                            message            = msg,
                            onReplay           = { if (!msg.isError) tts.speak(msg.text) },
                            onNavigateToStory  = onNavigateToStory,
                            onNavigateToRelax  = onNavigateToRelax,
                        )
                    }
                    if (uiState.isLoading) {
                        item {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier              = Modifier.padding(8.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SkyBlue80),
                                ) {
                                    Text("🌸", fontSize = 16.sp)
                                }
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(24.dp),
                                    color       = SkyBlue40,
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                }
            }

            // ─── Input row ───────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                // Mic button
                MicButton(
                    isListening = isListening,
                    onClick     = { if (isListening) stopListening() else startListening() },
                )

                // Text field
                OutlinedTextField(
                    value            = uiState.inputText,
                    onValueChange    = viewModel::onInputChange,
                    placeholder      = { Text(if (isListening) "Đang nghe..." else "Con muốn nói gì...") },
                    shape            = RoundedCornerShape(24.dp),
                    singleLine       = false,
                    maxLines         = 4,
                    keyboardOptions  = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions  = KeyboardActions(onSend = { viewModel.sendMessage() }),
                    modifier         = Modifier.weight(1f),
                )

                // Send button
                EmotionPrimaryButton(
                    text     = "Gửi",
                    onClick  = { viewModel.sendMessage() },
                    enabled  = uiState.inputText.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier.width(72.dp),
                )
            }
        }
    }
}

// ─── Mic button ──────────────────────────────────────────────────────────────

@Composable
private fun MicButton(
    isListening: Boolean,
    onClick    : () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isListening) Color(0xFFFF5252)
                else             SkyBlue40.copy(alpha = 0.2f)
            ),
    ) {
        TextButton(
            onClick  = onClick,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text     = if (isListening) "🔴" else "🎤",
                fontSize = 20.sp,
            )
        }
    }
}

// ─── Chat bubble ─────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(
    message           : ConfideMessage,
    onReplay          : () -> Unit,
    onNavigateToStory : () -> Unit,
    onNavigateToRelax : () -> Unit,
) {
    val isUser = message.role == MessageRole.USER

    Column(
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        modifier            = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment     = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier              = Modifier.fillMaxWidth(),
        ) {
            // cô Vy avatar
            if (!isUser) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SkyBlue80),
                ) {
                    Text("🌸", fontSize = 16.sp)
                }
                Spacer(Modifier.width(6.dp))
            }

            // Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart    = 16.dp,
                            topEnd      = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd   = if (isUser) 4.dp  else 16.dp,
                        )
                    )
                    .background(
                        when {
                            message.isError -> Color(0xFFFFEBEE)
                            isUser          -> SkyBlue40
                            else            -> EmotionCalmBg
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text  = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser && !message.isError) Color.White else OnSurface,
                )
            }

            // Child avatar
            if (isUser) {
                Spacer(Modifier.width(6.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MintGreen40.copy(alpha = 0.25f)),
                ) {
                    Text("🧒", fontSize = 16.sp)
                }
            }
        }

        // Suggestion action button (only for ASSISTANT messages with a suggestion)
        if (!isUser && message.suggestion != null) {
            Spacer(Modifier.height(6.dp))
            val emoji    : String
            val label    : String
            val navigate : () -> Unit
            when (message.suggestion) {
                ConfideSuggestion.STORY -> {
                    emoji    = "📶"
                    label    = "Nghe câu chuyện"
                    navigate = onNavigateToStory
                }
                ConfideSuggestion.RELAX -> {
                    emoji    = "🌈"
                    label    = "Thư giãn cùng nhạc"
                    navigate = onNavigateToRelax
                }
                ConfideSuggestion.BREATHING -> {
                    emoji    = "🌬️"
                    label    = "Hít thở cùng cô Vy"
                    navigate = onNavigateToRelax
                }
            }
            EmotionPrimaryButton(
                text     = "$emoji $label",
                onClick  = navigate,
                modifier = Modifier.padding(start = 38.dp),
            )
        }
    }
}
