package com.emotionfriend.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.ArrayDeque
import java.util.Locale

/**
 * Thin wrapper around Android [TextToSpeech] configured for Vietnamese.
 *
 * Create via [rememberTtsPlayer] in any composable. The engine is lazily
 * initialised and automatically shut down when the composable leaves the
 * composition.
 */
class TtsPlayer(context: Context) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private val pendingUtterances = ArrayDeque<String>()

    init {
        tts = TextToSpeech(context) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                val engine = tts
                val languageResult = engine?.setLanguage(Locale("vi", "VN"))
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine?.language = Locale.getDefault()
                }
                while (pendingUtterances.isNotEmpty()) {
                    engine?.speak(pendingUtterances.removeFirst(), TextToSpeech.QUEUE_ADD, null, null)
                }
            }
        }
    }

    /** Speaks [text] immediately, flushing any queued utterances. */
    fun speak(text: String) {
        val engine = tts
        if (ready && engine != null) {
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            pendingUtterances.clear()
            pendingUtterances.addLast(text)
        }
    }

    fun shutdown() {
        pendingUtterances.clear()
        tts?.shutdown()
        tts = null
        ready = false
    }
}

/**
 * Creates and remembers a [TtsPlayer] scoped to the current composable.
 * The player is shut down automatically via [DisposableEffect].
 */
@Composable
fun rememberTtsPlayer(): TtsPlayer {
    val context = LocalContext.current
    val player = remember { TtsPlayer(context) }
    DisposableEffect(player) {
        onDispose { player.shutdown() }
    }
    return player
}
