package com.emotionfriend.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.EmotionRepository
import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
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

data class LearnEmotionUiState(
    val isLoading: Boolean            = true,
    val currentCard: EmotionCard?     = null,
    val options: List<EmotionType>    = emptyList(),
    val selectedEmotion: EmotionType? = null,
    val isAnswerSubmitted: Boolean    = false,
    val isCorrect: Boolean?           = null,
    val feedbackMessage: String       = "",
    val questionIndex: Int            = 0,
    val totalQuestions: Int           = 0,
    val isSessionComplete: Boolean    = false
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class LearnEmotionViewModel @Inject constructor(
    private val emotionRepository: EmotionRepository,
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnEmotionUiState())
    val uiState: StateFlow<LearnEmotionUiState> = _uiState.asStateFlow()

    // Shuffled list of cards for this session
    private var sessionCards: List<EmotionCard> = emptyList()

    init {
        loadSession()
    }

    // ---------------------------------------------------------------------------
    // Public events
    // ---------------------------------------------------------------------------

    fun selectAnswer(emotionType: EmotionType) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.update { it.copy(selectedEmotion = emotionType) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        val selected = state.selectedEmotion ?: return
        val card     = state.currentCard    ?: return

        val correct = selected == card.type

        val feedback = if (correct) {
            "Đúng rồi! ${card.emoji} là ${card.label}. Giỏi lắm!"
        } else {
            "Chưa đúng. Đây là cảm xúc ${card.label} ${card.emoji}. Con thử lại lần sau nhé!"
        }

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                isCorrect         = correct,
                feedbackMessage   = feedback
            )
        }

        savePracticeAttempt(
            cardId         = card.id,
            selected       = selected,
            correct        = card.type,
            isCorrect      = correct
        )
    }

    fun nextQuestion() {
        val nextIndex = _uiState.value.questionIndex + 1
        if (nextIndex >= sessionCards.size) {
            _uiState.update { it.copy(isSessionComplete = true) }
        } else {
            showQuestion(nextIndex)
        }
    }

    fun resetSession() {
        loadSession()
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private fun loadSession() {
        _uiState.update { LearnEmotionUiState(isLoading = true) }
        viewModelScope.launch {
            val cards = emotionRepository.getAll().first()
            sessionCards = cards.shuffled()

            if (sessionCards.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            showQuestion(0)
        }
    }

    private fun showQuestion(index: Int) {
        val card    = sessionCards[index]
        val options = buildOptions(card.type, sessionCards.map { it.type }.distinct())

        _uiState.update {
            it.copy(
                isLoading         = false,
                currentCard       = card,
                options           = options,
                selectedEmotion   = null,
                isAnswerSubmitted  = false,
                isCorrect         = null,
                feedbackMessage   = "",
                questionIndex     = index,
                totalQuestions    = sessionCards.size,
                isSessionComplete = false
            )
        }
    }

    /**
     * Returns 4 shuffled options that always include [correct] plus up to 3 distractors.
     */
    private fun buildOptions(
        correct: EmotionType,
        allTypes: List<EmotionType>
    ): List<EmotionType> {
        val distractors = allTypes
            .filter { it != correct }
            .shuffled()
            .take(3)
        return (listOf(correct) + distractors).shuffled()
    }

    private fun savePracticeAttempt(
        cardId: String,
        selected: EmotionType,
        correct: EmotionType,
        isCorrect: Boolean
    ) {
        viewModelScope.launch {
            practiceRepository.insert(
                PracticeAttempt(
                    id              = UUID.randomUUID().toString(),
                    childId         = "default_child",
                    taskType        = "learn_emotion",
                    promptId        = cardId,
                    selectedEmotion = selected,
                    correctEmotion  = correct,
                    isCorrect       = isCorrect,
                    createdAt       = System.currentTimeMillis()
                )
            )
        }
    }
}
