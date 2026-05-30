package com.emotionfriend.feature.journal

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

// ---------------------------------------------------------------------------
// Phases
// ---------------------------------------------------------------------------

enum class JournalPhase { HISTORY, SELECT_EMOTION, RECORDING }

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class JournalUiState(
    val phase: JournalPhase           = JournalPhase.HISTORY,
    val allEntries: List<JournalEntry> = emptyList(),
    val selectedEmotion: EmotionType? = null,
    val recordingSecondsLeft: Int     = 5,
    val isLoading: Boolean            = true,
    val playingEntryId: String?       = null,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var pendingAudioPath: String?     = null
    private var mediaPlayer: MediaPlayer?     = null

    companion object {
        private const val CHILD_ID        = "default_child"
        private const val RECORDING_SECS  = 5
    }

    init {
        viewModelScope.launch {
            journalRepository.getByChildId(CHILD_ID).collect { entries ->
                _uiState.update {
                    it.copy(
                        allEntries = entries.sortedByDescending { e -> e.createdAt },
                        isLoading  = false,
                    )
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add-entry flow
    // ─────────────────────────────────────────────────────────────────────────

    fun startAddEntry() {
        _uiState.update { it.copy(phase = JournalPhase.SELECT_EMOTION, selectedEmotion = null) }
    }

    fun selectEmotion(emotionType: EmotionType) {
        _uiState.update { it.copy(selectedEmotion = emotionType) }
    }

    fun startRecording(context: Context) {
        // Save to filesDir (persistent) instead of cacheDir (may be deleted by OS)
        val audioDir = File(context.filesDir, "journal_audio").also { it.mkdirs() }
        val file = File(audioDir, "journal_${System.currentTimeMillis()}.m4a")
        pendingAudioPath = file.absolutePath

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        mediaRecorder = recorder

        _uiState.update {
            it.copy(phase = JournalPhase.RECORDING, recordingSecondsLeft = RECORDING_SECS)
        }
    }

    fun onRecordingTick() {
        val secs = _uiState.value.recordingSecondsLeft - 1
        if (secs <= 0) {
            stopRecording()
        } else {
            _uiState.update { it.copy(recordingSecondsLeft = secs) }
        }
    }

    fun stopRecording() {
        val path = pendingAudioPath
        try {
            mediaRecorder?.apply { stop(); release() }
        } catch (e: Exception) { /* ignore */ }
        mediaRecorder = null
        saveEntry(path)
    }

    fun cancelAddEntry() {
        releaseRecorder()
        _uiState.update {
            it.copy(phase = JournalPhase.HISTORY, selectedEmotion = null, recordingSecondsLeft = RECORDING_SECS)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Playback
    // ─────────────────────────────────────────────────────────────────────────

    fun togglePlayback(entry: JournalEntry) {
        val audioPath = entry.audioPath ?: return
        val currentPlaying = _uiState.value.playingEntryId

        if (currentPlaying == entry.id) {
            stopPlayback()
            return
        }

        stopPlayback()

        val player = try {
            MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                setOnCompletionListener { stopPlayback() }
                start()
            }
        } catch (e: Exception) {
            return
        }

        mediaPlayer = player
        _uiState.update { it.copy(playingEntryId = entry.id) }
    }

    fun stopPlayback() {
        try { mediaPlayer?.stop() } catch (e: Exception) { /* ignore */ }
        mediaPlayer?.release()
        mediaPlayer = null
        _uiState.update { it.copy(playingEntryId = null) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun saveEntry(audioPath: String?) {
        val emotion = _uiState.value.selectedEmotion ?: return
        viewModelScope.launch {
            journalRepository.insert(
                JournalEntry(
                    id          = UUID.randomUUID().toString(),
                    childId     = CHILD_ID,
                    emotionType = emotion,
                    note        = null,
                    audioPath   = audioPath,
                    createdAt   = System.currentTimeMillis(),
                )
            )
            _uiState.update {
                it.copy(
                    phase               = JournalPhase.HISTORY,
                    selectedEmotion     = null,
                    recordingSecondsLeft = RECORDING_SECS,
                )
            }
        }
    }

    private fun releaseRecorder() {
        try { mediaRecorder?.apply { stop(); release() } } catch (e: Exception) {}
        mediaRecorder = null
        pendingAudioPath = null
    }

    override fun onCleared() {
        super.onCleared()
        releaseRecorder()
        stopPlayback()
    }
}

