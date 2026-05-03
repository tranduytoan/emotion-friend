package com.emotionfriend.feature.situation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.data.repository.ScenarioRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
import com.emotionfriend.domain.model.ScenarioLesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class SituationUiState(
    val isLoading: Boolean             = true,
    val currentScenario: ScenarioLesson? = null,
    val selectedEmotion: EmotionType?  = null,
    val isAnswerSubmitted: Boolean     = false,
    val isCorrect: Boolean?            = null,
    val explanation: String            = "",
    val questionIndex: Int             = 0,
    val totalQuestions: Int            = 0,
    val isSessionComplete: Boolean     = false
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class SituationViewModel @Inject constructor(
    private val scenarioRepository: ScenarioRepository,
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SituationUiState())
    val uiState: StateFlow<SituationUiState> = _uiState.asStateFlow()

    private var sessionScenarios: List<ScenarioLesson> = emptyList()

    init {
        loadSession()
    }

    // ---------------------------------------------------------------------------
    // Public events
    // ---------------------------------------------------------------------------

    fun selectEmotion(emotionType: EmotionType) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.update { it.copy(selectedEmotion = emotionType) }
    }

    fun submitAnswer() {
        val state    = _uiState.value
        val selected = state.selectedEmotion ?: return
        val scenario = state.currentScenario ?: return

        val correct = selected == scenario.correctEmotion

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                isCorrect         = correct,
                explanation       = scenario.explanation
            )
        }

        savePracticeAttempt(
            scenarioId = scenario.id,
            selected   = selected,
            correct    = scenario.correctEmotion,
            isCorrect  = correct
        )
    }

    fun nextScenario() {
        val nextIndex = _uiState.value.questionIndex + 1
        if (nextIndex >= sessionScenarios.size) {
            _uiState.update { it.copy(isSessionComplete = true) }
        } else {
            showScenario(nextIndex)
        }
    }

    fun resetSession() {
        loadSession()
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private fun loadSession() {
        _uiState.update { SituationUiState(isLoading = true) }
        viewModelScope.launch {
            val scenarios = scenarioRepository.getAll().first()
            sessionScenarios = scenarios.shuffled()

            if (sessionScenarios.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            showScenario(0)
        }
    }

    private fun showScenario(index: Int) {
        val scenario = sessionScenarios[index]

        _uiState.update {
            it.copy(
                isLoading         = false,
                currentScenario   = scenario,
                selectedEmotion   = null,
                isAnswerSubmitted  = false,
                isCorrect         = null,
                explanation       = "",
                questionIndex     = index,
                totalQuestions    = sessionScenarios.size,
                isSessionComplete = false
            )
        }
    }

    private fun savePracticeAttempt(
        scenarioId: String,
        selected: EmotionType,
        correct: EmotionType,
        isCorrect: Boolean
    ) {
        viewModelScope.launch {
            practiceRepository.insert(
                PracticeAttempt(
                    id              = UUID.randomUUID().toString(),
                    childId         = "default_child",
                    taskType        = "situation",
                    promptId        = scenarioId,
                    selectedEmotion = selected,
                    correctEmotion  = correct,
                    isCorrect       = isCorrect,
                    createdAt       = System.currentTimeMillis()
                )
            )
        }
    }
}
