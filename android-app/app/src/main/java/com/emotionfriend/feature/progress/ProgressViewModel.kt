package com.emotionfriend.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.ProgressRepository
import com.emotionfriend.domain.model.EmotionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class ProgressUiState(
    val isLoading: Boolean          = true,
    val completedLessons: Int       = 0,
    val accuracyRate: Float         = 0f,
    val mostMistakenEmotion: EmotionType? = null,
    val journalCount: Int           = 0,
    val encouragementMessage: String = ""
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    companion object {
        private const val CHILD_ID = "default_child"
    }

    init {
        observeSummary()
    }

    private fun observeSummary() {
        viewModelScope.launch {
            progressRepository.getSummary(CHILD_ID).collect { summary ->
                val message = when {
                    summary.completedLessons == 0 && summary.journalCount == 0 ->
                        "Con hãy bắt đầu bài học đầu tiên nhé."
                    summary.accuracyRate >= 0.8f ->
                        "Con đang làm rất tốt! 🌟"
                    else ->
                        "Mình cùng luyện thêm một chút nhé. 💪"
                }
                _uiState.update {
                    it.copy(
                        isLoading            = false,
                        completedLessons     = summary.completedLessons,
                        accuracyRate         = summary.accuracyRate,
                        mostMistakenEmotion  = summary.mostMistakenEmotion,
                        journalCount         = summary.journalCount,
                        encouragementMessage = message
                    )
                }
            }
        }
    }
}
