package com.emotionfriend.feature.express

import androidx.lifecycle.ViewModel
import com.emotionfriend.domain.model.EmotionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class PermissionState { UNKNOWN, GRANTED, DENIED }
enum class CaptureState   { IDLE, DONE }

data class ExpressUiState(
    val permissionState: PermissionState = PermissionState.UNKNOWN,
    val captureState   : CaptureState    = CaptureState.IDLE,
    val promptEmotion  : EmotionType     = EmotionType.HAPPY,
    val feedbackMessage: String          = ""
)

@HiltViewModel
class ExpressCameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ExpressUiState())
    val uiState: StateFlow<ExpressUiState> = _uiState.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(permissionState = if (granted) PermissionState.GRANTED else PermissionState.DENIED)
        }
    }

    fun onCapture() {
        _uiState.update {
            it.copy(
                captureState    = CaptureState.DONE,
                feedbackMessage = "Con làm tốt lắm! 🌟"
            )
        }
    }

    fun onRetry() {
        _uiState.update {
            it.copy(captureState = CaptureState.IDLE, feedbackMessage = "")
        }
    }

    fun nextPrompt() {
        val next = EmotionType.entries.toList()
            .filter { it != _uiState.value.promptEmotion }
            .random()
        _uiState.update {
            it.copy(
                promptEmotion   = next,
                captureState    = CaptureState.IDLE,
                feedbackMessage = ""
            )
        }
    }
}
