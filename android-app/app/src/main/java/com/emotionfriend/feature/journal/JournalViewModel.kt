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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class JournalUiState(
    val emotionOptions: List<EmotionType>  = EmotionType.entries,
    val selectedEmotion: EmotionType?      = null,
    val saveSuccess: Boolean               = false,
    val recentEntries: List<JournalEntry>  = emptyList()
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    companion object {
        private const val CHILD_ID       = "default_child"
        private const val RECENT_LIMIT   = 3
    }

    init {
        observeRecentEntries()
    }

    // ---------------------------------------------------------------------------
    // Public events
    // ---------------------------------------------------------------------------

    fun selectEmotion(emotionType: EmotionType) {
        _uiState.update { it.copy(selectedEmotion = emotionType, saveSuccess = false) }
    }

    fun saveCurrentEmotion() {
        val emotion = _uiState.value.selectedEmotion ?: return
        viewModelScope.launch {
            journalRepository.insert(
                JournalEntry(
                    id          = UUID.randomUUID().toString(),
                    childId     = CHILD_ID,
                    emotionType = emotion,
                    note        = null,
                    createdAt   = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(selectedEmotion = null, saveSuccess = true) }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private fun observeRecentEntries() {
        viewModelScope.launch {
            journalRepository.getByChildId(CHILD_ID).collect { entries ->
                val recent = entries
                    .sortedByDescending { it.createdAt }
                    .take(RECENT_LIMIT)
                _uiState.update { it.copy(recentEntries = recent) }
            }
        }
    }
}
