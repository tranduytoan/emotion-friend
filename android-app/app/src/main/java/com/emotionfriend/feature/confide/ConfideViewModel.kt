package com.emotionfriend.feature.confide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.remote.ApiResult
import com.emotionfriend.data.remote.OpenAIApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MessageRole { USER, ASSISTANT }

/** Navigation suggestion from cô Vy: which feature to open next. */
enum class ConfideSuggestion(val label: String, val emoji: String) {
    STORY("Nghe câu chuyện", "📖"),
    RELAX("Thư giãn", "🌈"),
    BREATHING("Hít thở cùng cô", "🌬️"),
}

data class ConfideMessage(
    val role       : MessageRole,
    val text       : String,
    val isError    : Boolean = false,
    val suggestion : ConfideSuggestion? = null,
)

data class ConfideUiState(
    val messages   : List<ConfideMessage> = emptyList(),
    val isLoading  : Boolean = false,
    val inputText  : String  = "",
)

@HiltViewModel
class ConfideViewModel @Inject constructor(
    private val openAI: OpenAIApiClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfideUiState())
    val uiState: StateFlow<ConfideUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    /** Called from both keyboard send and STT mic. */
    fun sendMessage(textOverride: String? = null) {
        val text = (textOverride ?: _uiState.value.inputText).trim()
        if (text.isBlank()) return

        val userMsg = ConfideMessage(role = MessageRole.USER, text = text)
        _uiState.update { state ->
            state.copy(
                messages  = state.messages + userMsg,
                inputText = "",
                isLoading = true,
            )
        }

        viewModelScope.launch {
            when (val result = openAI.chat(text)) {
                is ApiResult.Success -> {
                    val raw        = result.data
                    val suggestion = parseSuggestion(raw)
                    val displayText = raw
                        .replace("[SUGGEST:STORY]",     "")
                        .replace("[SUGGEST:RELAX]",     "")
                        .replace("[SUGGEST:BREATHING]", "")
                        .trim()
                    val botMsg = ConfideMessage(
                        role       = MessageRole.ASSISTANT,
                        text       = displayText,
                        suggestion = suggestion,
                    )
                    _uiState.update { it.copy(messages = it.messages + botMsg, isLoading = false) }
                }
                is ApiResult.Error -> {
                    // API failed — use keyword fallback so conversation still works
                    val fallback = buildFallbackResponse(text)
                    _uiState.update {
                        it.copy(
                            messages  = it.messages + fallback,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    // ─── Suggestion parsing ──────────────────────────────────────────────────

    private fun parseSuggestion(text: String): ConfideSuggestion? = when {
        "[SUGGEST:STORY]"     in text -> ConfideSuggestion.STORY
        "[SUGGEST:RELAX]"     in text -> ConfideSuggestion.RELAX
        "[SUGGEST:BREATHING]" in text -> ConfideSuggestion.BREATHING
        else                          -> null
    }

    // ─── Keyword-based fallback (no API needed) ──────────────────────────────

    private fun buildFallbackResponse(input: String): ConfideMessage {
        val lo = input.lowercase()
        val (text, suggestion) = when {
            containsAny(lo, "buồn", "khóc", "không vui", "không hạnh phúc", "sad") ->
                "Cô Vy hiểu con đang buồn. Con không cần phải giữ điều đó một mình đâu nhé 💛 Để cô kể một câu chuyện giúp con vui lên nhé?" to
                    ConfideSuggestion.STORY

            containsAny(lo, "kể chuyện", "nghe chuyện", "câu chuyện", "truyện") ->
                "Cô có nhiều câu chuyện hay lắm! Mình cùng vào nghe nhé 📖" to
                    ConfideSuggestion.STORY

            containsAny(lo, "tức", "giận", "bực", "nổi giận", "angry") ->
                "Khi tức giận, hít thở sâu sẽ giúp con bình tĩnh hơn đó! Mình thử cùng nhau nhé 🌬️" to
                    ConfideSuggestion.BREATHING

            containsAny(lo, "lo", "sợ", "lo lắng", "hồi hộp", "afraid", "scared") ->
                "Con đang lo lắng à? Cô Vy ở đây với con. Hít thở nhẹ nhàng sẽ giúp con bình tĩnh hơn nhé! 🌬️" to
                    ConfideSuggestion.BREATHING

            containsAny(lo, "mệt", "mệt mỏi", "kiệt sức", "tired", "exhausted") ->
                "Con có vẻ mệt rồi. Nghe nhạc nhẹ sẽ giúp con thư giãn đó! Mình vào thư giãn cùng nhau nhé? 🌈" to
                    ConfideSuggestion.RELAX

            containsAny(lo, "thư giãn", "nghỉ ngơi", "âm nhạc", "nhạc", "relax") ->
                "Nghe nhạc nhẹ nhàng thật dễ chịu! Mình vào nghe nhé 🎵" to
                    ConfideSuggestion.RELAX

            containsAny(lo, "vui", "hạnh phúc", "thích", "vui quá", "happy") ->
                "Thật tuyệt vời khi con cảm thấy vui! Cô Vy cũng vui lây đây 😊 Con muốn chia sẻ thêm không?" to
                    null

            else ->
                "Cô Vy lắng nghe con rồi. Con có muốn kể thêm cho cô nghe không?" to
                    null
        }
        return ConfideMessage(role = MessageRole.ASSISTANT, text = text, suggestion = suggestion)
    }

    private fun containsAny(haystack: String, vararg needles: String) =
        needles.any { it in haystack }
}

