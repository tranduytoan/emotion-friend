package com.emotionfriend.feature.profile

import androidx.lifecycle.ViewModel
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class ProfileSettings(
    val soundEnabled: Boolean,
    val notificationEnabled: Boolean,
    val reminderTime: String,
    val language: String
)

data class ProfileProgress(
    val totalExercises: Int,
    val correctAnswers: Int,
    val currentStreak: Int,
    val longestStreak: Int
)

data class ProfileUiState(
    val userId: Long,
    val name: String,
    val age: Int,
    val avatarEmoji: String,
    val accentColor: androidx.compose.ui.graphics.Color,
    val accentBackground: androidx.compose.ui.graphics.Color,
    val settings: ProfileSettings,
    val progress: ProfileProgress
)

// ---------------------------------------------------------------------------
// ViewModel (mock/local state only)
// ---------------------------------------------------------------------------

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            userId = 1,
            name = "Bé Minh",
            age = 8,
            avatarEmoji = "🧒",
            accentColor = EmotionHappy,
            accentBackground = EmotionHappyBg,
            settings = ProfileSettings(
                soundEnabled = true,
                notificationEnabled = true,
                reminderTime = "19:00",
                language = "Tiếng Việt"
            ),
            progress = ProfileProgress(
                totalExercises = 0,
                correctAnswers = 0,
                currentStreak = 0,
                longestStreak = 0
            )
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateProfile(name: String, age: Int) {
        _uiState.value = _uiState.value.copy(
            name = name,
            age = age
        )
    }

    fun updateSettings(
        soundEnabled: Boolean,
        notificationEnabled: Boolean,
        reminderTime: String,
        language: String
    ) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(
                soundEnabled = soundEnabled,
                notificationEnabled = notificationEnabled,
                reminderTime = reminderTime,
                language = language
            )
        )
    }
}
