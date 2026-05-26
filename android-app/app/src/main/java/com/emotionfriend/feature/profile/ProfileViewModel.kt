package com.emotionfriend.feature.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
// ViewModel — profile data persisted via DataStore
// ---------------------------------------------------------------------------

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    companion object {
        private val KEY_NAME                = stringPreferencesKey("profile_name")
        private val KEY_AGE                 = intPreferencesKey("profile_age")
        private val KEY_AVATAR              = stringPreferencesKey("profile_avatar")
        private val KEY_SOUND               = booleanPreferencesKey("profile_sound")
        private val KEY_NOTIFICATIONS       = booleanPreferencesKey("profile_notifications")
        private val KEY_REMINDER_TIME       = stringPreferencesKey("profile_reminder_time")
        private val KEY_LANGUAGE            = stringPreferencesKey("profile_language")

        // Defaults
        private const val DEFAULT_NAME          = "Bé Minh"
        private const val DEFAULT_AGE           = 8
        private const val DEFAULT_AVATAR        = "🧒"
        private const val DEFAULT_REMINDER_TIME = "19:00"
        private const val DEFAULT_LANGUAGE      = "Tiếng Việt"
    }

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            userId          = 1,
            name            = DEFAULT_NAME,
            age             = DEFAULT_AGE,
            avatarEmoji     = DEFAULT_AVATAR,
            accentColor     = EmotionHappy,
            accentBackground = EmotionHappyBg,
            settings        = ProfileSettings(
                soundEnabled        = true,
                notificationEnabled = true,
                reminderTime        = DEFAULT_REMINDER_TIME,
                language            = DEFAULT_LANGUAGE,
            ),
            progress        = ProfileProgress(
                totalExercises = 0,
                correctAnswers = 0,
                currentStreak  = 0,
                longestStreak  = 0,
            ),
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadPersistedProfile() }
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    private suspend fun loadPersistedProfile() {
        val prefs = dataStore.data.first()
        _uiState.value = _uiState.value.copy(
            name        = prefs[KEY_NAME]    ?: DEFAULT_NAME,
            age         = prefs[KEY_AGE]     ?: DEFAULT_AGE,
            avatarEmoji = prefs[KEY_AVATAR]  ?: DEFAULT_AVATAR,
            settings    = ProfileSettings(
                soundEnabled        = prefs[KEY_SOUND]         ?: true,
                notificationEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
                reminderTime        = prefs[KEY_REMINDER_TIME] ?: DEFAULT_REMINDER_TIME,
                language            = prefs[KEY_LANGUAGE]      ?: DEFAULT_LANGUAGE,
            ),
        )
    }

    // ── Write ────────────────────────────────────────────────────────────────

    fun updateProfile(name: String, age: Int) {
        _uiState.value = _uiState.value.copy(name = name, age = age)
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_NAME] = name
                prefs[KEY_AGE]  = age
            }
        }
    }

    fun updateAvatar(emoji: String) {
        _uiState.value = _uiState.value.copy(avatarEmoji = emoji)
        viewModelScope.launch {
            dataStore.edit { prefs -> prefs[KEY_AVATAR] = emoji }
        }
    }

    fun updateSettings(
        soundEnabled: Boolean,
        notificationEnabled: Boolean,
        reminderTime: String,
        language: String,
    ) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(
                soundEnabled        = soundEnabled,
                notificationEnabled = notificationEnabled,
                reminderTime        = reminderTime,
                language            = language,
            ),
        )
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_SOUND]         = soundEnabled
                prefs[KEY_NOTIFICATIONS] = notificationEnabled
                prefs[KEY_REMINDER_TIME] = reminderTime
                prefs[KEY_LANGUAGE]      = language
            }
        }
    }
}
