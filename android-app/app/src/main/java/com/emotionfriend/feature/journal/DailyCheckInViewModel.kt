package com.emotionfriend.feature.journal

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
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class DailyCheckInUiState(
    /** True while the VM is checking time/db conditions on init. */
    val isLoading: Boolean = true,
    /** True if current time is in the 18:00–23:59 check-in window. */
    val canCheckIn: Boolean = false,
    /** True if today's check-in was already saved. */
    val alreadySavedToday: Boolean = false,
    val selectedEmotion: EmotionType? = null,
    /** Absolute path of an optional voice recording. */
    val audioPath: String? = null,
    val saveSuccess: Boolean = false,
    /** Emitted when the screen should navigate away to Home. */
    val shouldNavigateToHome: Boolean = false,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class DailyCheckInViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyCheckInUiState())
    val uiState: StateFlow<DailyCheckInUiState> = _uiState.asStateFlow()

    companion object {
        private const val CHILD_ID = "default_child"
        private const val CHECK_IN_HOUR_START = 18
    }

    init {
        checkConditions()
    }

    // ── Public events ────────────────────────────────────────────────────────

    fun selectEmotion(type: EmotionType) {
        _uiState.update { it.copy(selectedEmotion = type) }
    }

    fun setAudioPath(path: String) {
        _uiState.update { it.copy(audioPath = path) }
    }

    fun saveCheckIn() {
        val emotion = _uiState.value.selectedEmotion ?: return
        viewModelScope.launch {
            journalRepository.insert(
                JournalEntry(
                    id          = UUID.randomUUID().toString(),
                    childId     = CHILD_ID,
                    emotionType = emotion,
                    note        = _uiState.value.audioPath,
                    createdAt   = System.currentTimeMillis(),
                )
            )
            _uiState.update { it.copy(saveSuccess = true, shouldNavigateToHome = true) }
        }
    }

    fun skipCheckIn() {
        _uiState.update { it.copy(shouldNavigateToHome = true) }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun checkConditions() {
        viewModelScope.launch {
            val cal  = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val inWindow = hour in CHECK_IN_HOUR_START..23

            val alreadySaved = if (inWindow) {
                // Build timestamp for 18:00 today
                val startOfWindow = cal.clone() as Calendar
                startOfWindow.set(Calendar.HOUR_OF_DAY, CHECK_IN_HOUR_START)
                startOfWindow.set(Calendar.MINUTE, 0)
                startOfWindow.set(Calendar.SECOND, 0)
                startOfWindow.set(Calendar.MILLISECOND, 0)

                val entries = journalRepository.getByChildId(CHILD_ID).first()
                entries.any { it.createdAt >= startOfWindow.timeInMillis }
            } else {
                false
            }

            _uiState.update {
                it.copy(
                    isLoading            = false,
                    canCheckIn           = inWindow,
                    alreadySavedToday    = alreadySaved,
                    shouldNavigateToHome = !inWindow || alreadySaved,
                )
            }
        }
    }
}
