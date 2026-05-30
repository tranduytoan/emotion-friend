package com.emotionfriend.feature.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

/** Phase of the daily welcome + check-in flow shown on the Home screen. */
enum class CheckInPhase {
    /** Greeting audio playing, waiting before showing emotion picker. */
    WELCOME,
    /** Emotion grid shown — user selects their feeling. */
    SELECT_EMOTION,
    /** 5-second voice recording countdown. */
    RECORDING,
    /** Check-in complete — show the normal activity carousel. */
    DONE,
}

data class HomeUiState(
    val checkInPhase: CheckInPhase = CheckInPhase.WELCOME,
    /** True only on the very first visit of the day — greeting differs. */
    val isFirstVisitToday: Boolean = true,
    val selectedEmotion: EmotionType? = null,
    val recordingSecondsLeft: Int = 5,
    val audioPath: String? = null,
    val isLoading: Boolean = true,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null

    companion object {
        private const val CHILD_ID = "default_child"
        private val KEY_LAST_CHECK_IN_DATE = stringPreferencesKey("home_last_checkin_date")
        private val KEY_LAST_HOME_VISIT_DATE = stringPreferencesKey("home_last_visit_date")
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    init {
        viewModelScope.launch { checkDailyState() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun checkDailyState() {
        val prefs   = dataStore.data.first()
        val today   = DATE_FORMAT.format(Date())

        // Check first visit today (not used for skipping, just metadata)
        val lastVisit = prefs[KEY_LAST_HOME_VISIT_DATE]
        val isFirstVisitToday = lastVisit != today

        // Record this visit
        dataStore.edit { it[KEY_LAST_HOME_VISIT_DATE] = today }

        // Always start at SELECT_EMOTION — emotion check-in on every app open
        _uiState.update {
            it.copy(
                isLoading         = false,
                isFirstVisitToday = isFirstVisitToday,
                checkInPhase      = CheckInPhase.SELECT_EMOTION,
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public events
    // ─────────────────────────────────────────────────────────────────────────

    fun onWelcomeFinished() {
        _uiState.update { it.copy(checkInPhase = CheckInPhase.SELECT_EMOTION) }
    }

    fun selectEmotion(type: EmotionType) {
        _uiState.update {
            it.copy(selectedEmotion = type, checkInPhase = CheckInPhase.RECORDING)
        }
    }

    fun onRecordingTick() {
        val current = _uiState.value.recordingSecondsLeft
        if (current > 1) {
            _uiState.update { it.copy(recordingSecondsLeft = current - 1) }
        } else {
            stopRecording()
        }
    }

    fun startRecording(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Skip recording if no permission — still proceed to DONE
            finishCheckIn(null)
            return
        }

        val file = File(context.cacheDir, "home_checkin_${System.currentTimeMillis()}.3gp")
        recordingFile = file

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
            runCatching { prepare(); start() }
        }
        mediaRecorder = recorder
        _uiState.update { it.copy(recordingSecondsLeft = 5) }
    }

    private fun stopRecording() {
        mediaRecorder?.runCatching { stop(); release() }
        mediaRecorder = null
        val path = recordingFile?.absolutePath
        finishCheckIn(path)
    }

    private fun finishCheckIn(audioPath: String?) {
        val emotion = _uiState.value.selectedEmotion ?: run {
            _uiState.update { it.copy(checkInPhase = CheckInPhase.DONE) }
            return
        }

        viewModelScope.launch {
            val today = DATE_FORMAT.format(Date())
            dataStore.edit { it[KEY_LAST_CHECK_IN_DATE] = today }

            journalRepository.insert(
                JournalEntry(
                    id          = UUID.randomUUID().toString(),
                    childId     = CHILD_ID,
                    emotionType = emotion,
                    note        = null,
                    createdAt   = System.currentTimeMillis(),
                    audioPath   = audioPath,
                )
            )
            _uiState.update {
                it.copy(checkInPhase = CheckInPhase.DONE, audioPath = audioPath)
            }
        }
    }

    /** Skip check-in (user taps skip or it's already done). */
    fun skipCheckIn() {
        _uiState.update { it.copy(checkInPhase = CheckInPhase.DONE) }
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.runCatching { stop(); release() }
        mediaRecorder = null
    }
}
