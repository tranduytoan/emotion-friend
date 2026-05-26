package com.emotionfriend.feature.parent

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.data.repository.ProgressRepository
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class ParentDashboardUiState(
    val isLoading: Boolean                    = true,
    val childName: String                     = "Bé Minh",
    val childAge: Int                         = 8,
    val childAvatar: String                   = "🧒",
    val completedLessons: Int                 = 0,
    val accuracyRate: Float                   = 0f,
    val journalCount: Int                     = 0,
    val mostMistakenEmotion: EmotionType?     = null,
    val recentEntries: List<JournalEntry>     = emptyList(),
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val journalRepository: JournalRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    companion object {
        private const val CHILD_ID          = "default_child"
        private val KEY_NAME                = stringPreferencesKey("profile_name")
        private val KEY_AGE                 = intPreferencesKey("profile_age")
        private val KEY_AVATAR              = stringPreferencesKey("profile_avatar")
        private const val RECENT_ENTRY_LIMIT = 5
    }

    private val _uiState = MutableStateFlow(ParentDashboardUiState())
    val uiState: StateFlow<ParentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            // Read persisted child profile from DataStore (written by ProfileViewModel)
            val prefs  = dataStore.data.first()
            val name   = prefs[KEY_NAME]   ?: "Bé Minh"
            val age    = prefs[KEY_AGE]    ?: 8
            val avatar = prefs[KEY_AVATAR] ?: "🧒"

            // Combine progress summary + full journal list reactively
            combine(
                progressRepository.getSummary(CHILD_ID),
                journalRepository.getByChildId(CHILD_ID),
            ) { summary, entries ->
                ParentDashboardUiState(
                    isLoading           = false,
                    childName           = name,
                    childAge            = age,
                    childAvatar         = avatar,
                    completedLessons    = summary.completedLessons,
                    accuracyRate        = summary.accuracyRate,
                    journalCount        = summary.journalCount,
                    mostMistakenEmotion = summary.mostMistakenEmotion,
                    recentEntries       = entries.take(RECENT_ENTRY_LIMIT),
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
