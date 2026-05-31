package com.emotionfriend.feature.learn

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.local.LessonTopicDao
import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.data.repository.ScenarioImagePreloadRepository
import com.emotionfriend.data.repository.ScenarioRepository
import com.emotionfriend.domain.model.EmotionCard
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
// Domain types used only within this feature
// ---------------------------------------------------------------------------

enum class LessonType { SCENARIO }
enum class LearnPhase { SETS_LIST, QUESTION, SET_COMPLETE }

data class LessonSetInfo(
    val id: String,
    val title: String,
    val type: LessonType,
    val totalCount: Int,
    val correctCount: Int,
) {
    val isComplete: Boolean get() = correctCount >= totalCount
    val progressFraction: Float get() = if (totalCount == 0) 0f else correctCount.toFloat() / totalCount
}

/** Normalised question regardless of type (emotion card or scenario). */
data class ActiveQuestion(
    val id: String,
    val prompt: String,
    val imageName: String? = null,
    val options: List<EmotionType>,
    val correctAnswer: EmotionType,
    val subtitle: String = "",          // used for scenario title
)

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class LearnEmotionUiState(
    val isLoading: Boolean               = true,
    val phase: LearnPhase                = LearnPhase.SETS_LIST,
    val lessonSets: List<LessonSetInfo>  = emptyList(),
    val activeSetId: String?             = null,
    val currentQuestion: ActiveQuestion? = null,
    val selectedEmotion: EmotionType?    = null,
    val isAnswerSubmitted: Boolean       = false,
    val isCorrect: Boolean?              = null,
    val feedbackMessage: String          = "",
    val questionIndex: Int               = 0,
    val totalQuestionsInSet: Int         = 0,
) {
    // Keep old fields for backward compat with LearnScreen question view
    val currentCard: EmotionCard? = null
    val isChallengeMode: Boolean  = false
    val isSessionComplete: Boolean get() = phase == LearnPhase.SET_COMPLETE
}

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class LearnEmotionViewModel @Inject constructor(
    private val lessonTopicDao: LessonTopicDao,
    private val scenarioRepository: ScenarioRepository,
    private val scenarioImagePreloadRepository: ScenarioImagePreloadRepository,
    private val practiceRepository: PracticeRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnEmotionUiState())
    val uiState: StateFlow<LearnEmotionUiState> = _uiState.asStateFlow()

    /** Maps setId → ordered list of ActiveQuestion */
    private val questionsBySet: MutableMap<String, List<ActiveQuestion>> = mutableMapOf()

    /** Current ordered list for the active set (wrong/unanswered first, stable within a session). */
    private var currentOrderedList: List<ActiveQuestion> = emptyList()

    init {
        loadSets()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Set list
    // ─────────────────────────────────────────────────────────────────────────

    private fun loadSets() {
        viewModelScope.launch {
            val scenarios = scenarioRepository.getAll().first()
                .filter { it.topicId != null }
                .distinctBy { "${it.topicId}|${it.title.trim()}|${it.situationText.trim()}" }
            scenarioImagePreloadRepository.preload(scenarios.map { it.imageName })
            val topics = lessonTopicDao.getAll().first()
            val prefs = dataStore.data.first()

            questionsBySet.clear()

            val scenariosByTopic = scenarios.groupBy { it.topicId!! }

            val dbTopicSets = topics.mapNotNull { topic ->
                val topicScenarios = scenariosByTopic[topic.id].orEmpty()
                    .sortedBy { it.title }
                if (topicScenarios.isEmpty()) return@mapNotNull null

                val setId = "scenario_topic_${topic.id}"
                val questions = buildScenarioQuestions(topicScenarios)
                questionsBySet[setId] = questions
                val correct = prefs.correctCount(setId, questions.map { it.id }.toSet())

                LessonSetInfo(
                    id = setId,
                    title = topic.title,
                    type = LessonType.SCENARIO,
                    totalCount = questions.size,
                    correctCount = correct,
                )
            }

            val fallbackSets = if (dbTopicSets.isNotEmpty()) {
                emptyList()
            } else {
                scenariosByTopic.entries
                    .sortedBy { it.key }
                    .map { (topicId, topicScenarios) ->
                        val setId = "scenario_topic_$topicId"
                        val questions = buildScenarioQuestions(topicScenarios)
                        questionsBySet[setId] = questions
                        val correct = prefs.correctCount(setId, questions.map { it.id }.toSet())

                        LessonSetInfo(
                            id = setId,
                            title = "Chủ đề #$topicId",
                            type = LessonType.SCENARIO,
                            totalCount = questions.size,
                            correctCount = correct,
                        )
                    }
            }

            _uiState.update {
                it.copy(
                    isLoading  = false,
                    lessonSets = dbTopicSets + fallbackSets,
                    phase      = LearnPhase.SETS_LIST,
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Open a set
    // ─────────────────────────────────────────────────────────────────────────

    fun openSet(setId: String) {
        viewModelScope.launch {
            val prefs     = dataStore.data.first()
            val allQuestions = questionsBySet[setId] ?: return@launch
            val correctIds   = prefs[progressKey(setId)] ?: emptySet()

            // Wrong / unanswered first, already-correct last — stable for this session
            val ordered = allQuestions.sortedBy { if (it.id in correctIds) 1 else 0 }
            currentOrderedList = ordered

            _uiState.update {
                it.copy(
                    phase              = LearnPhase.QUESTION,
                    activeSetId        = setId,
                    questionIndex      = 0,
                    totalQuestionsInSet = ordered.size,
                    selectedEmotion    = null,
                    isAnswerSubmitted  = false,
                    isCorrect          = null,
                    feedbackMessage    = "",
                )
            }
            showQuestion(ordered, 0)
        }
    }

    fun openFirstIncompleteSet() {
        val target = _uiState.value.lessonSets.firstOrNull { !it.isComplete }
        target?.let { openSet(it.id) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Answer handling
    // ─────────────────────────────────────────────────────────────────────────

    fun selectAnswer(emotionType: EmotionType) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.update { it.copy(selectedEmotion = emotionType) }
    }

    fun submitAnswer() {
        val state    = _uiState.value
        val selected = state.selectedEmotion   ?: return
        val question = state.currentQuestion   ?: return

        val correct  = selected == question.correctAnswer
        val feedback = if (correct)
            "Đúng rồi! Giỏi lắm! 🎉"
        else
            "Chưa đúng. Đáp án là ${question.correctAnswer.name.lowercase()}. Con thử lại lần sau nhé!"

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                isCorrect         = correct,
                feedbackMessage   = feedback,
            )
        }

        if (correct) {
            viewModelScope.launch {
                val setId = state.activeSetId ?: return@launch
                dataStore.edit { prefs ->
                    val existing = prefs[progressKey(setId)] ?: emptySet()
                    prefs[progressKey(setId)] = existing + question.id
                }
            }
        }

        savePracticeAttempt(
            cardId    = question.id,
            selected  = selected,
            correct   = question.correctAnswer,
            isCorrect = correct,
        )
    }

    fun nextQuestion() {
        val state        = _uiState.value
        val setId        = state.activeSetId ?: return
        val ordered      = currentOrderedList.ifEmpty { questionsBySet[setId] ?: return }

        viewModelScope.launch {
            val prefs      = dataStore.data.first()
            val nextIndex = state.questionIndex + 1
            if (nextIndex >= ordered.size) {
                // Refresh correct count in sets list
                val correctIds = prefs[progressKey(setId)] ?: emptySet()
                val correct = correctIds.intersect(ordered.map { it.id }.toSet()).size
                _uiState.update { s ->
                    val updatedSets = s.lessonSets.map {
                        if (it.id == setId) it.copy(correctCount = correct) else it
                    }
                    s.copy(
                        phase      = LearnPhase.SET_COMPLETE,
                        lessonSets = updatedSets,
                    )
                }
            } else {
                showQuestion(ordered, nextIndex)
            }
        }
    }

    fun backToSetsList() {
        _uiState.update { it.copy(phase = LearnPhase.SETS_LIST, activeSetId = null) }
    }

    fun resetSession() {
        _uiState.update { it.copy(phase = LearnPhase.SETS_LIST, activeSetId = null) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun showQuestion(questions: List<ActiveQuestion>, index: Int) {
        _uiState.update {
            it.copy(
                currentQuestion    = questions[index],
                questionIndex      = index,
                totalQuestionsInSet = questions.size,
                selectedEmotion    = null,
                isAnswerSubmitted  = false,
                isCorrect          = null,
                feedbackMessage    = "",
            )
        }
    }

    private fun buildScenarioQuestions(chunk: List<ScenarioLesson>): List<ActiveQuestion> =
        chunk.map { lesson ->
            ActiveQuestion(
                id            = lesson.id,
                prompt        = "${lesson.situationText} Con cảm thấy thế nào?",
                imageName     = lesson.imageName,
                options       = lesson.options,
                correctAnswer = lesson.correctEmotion,
                subtitle      = lesson.title,
            )
        }

    private fun progressKey(setId: String) = stringSetPreferencesKey("learn_progress_$setId")

    private fun Preferences.correctCount(setId: String, allIds: Set<String>): Int =
        (this[progressKey(setId)] ?: emptySet()).intersect(allIds).size

    private fun savePracticeAttempt(
        cardId: String,
        selected: EmotionType,
        correct: EmotionType,
        isCorrect: Boolean,
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
                    createdAt       = System.currentTimeMillis(),
                )
            )
        }
    }
}

