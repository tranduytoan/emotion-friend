package com.emotionfriend.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists and retrieves the authenticated user session using DataStore.
 *
 * Keys are intentionally minimal — we never store passwords or tokens here.
 * For a production backend, replace [saveSession] with a JWT/token mechanism.
 */
@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val KEY_USER_ID       = stringPreferencesKey("auth_user_id")
        private val KEY_EMAIL         = stringPreferencesKey("auth_email")
        private val KEY_DISPLAY_NAME  = stringPreferencesKey("auth_display_name")
        private val KEY_ROLE          = stringPreferencesKey("auth_role")
        private val KEY_IS_VERIFIED   = booleanPreferencesKey("auth_is_verified")
    }

    /** Emits the persisted [AuthUser], or `null` when no session exists. */
    val sessionFlow: Flow<AuthUser?> = dataStore.data.map { prefs ->
        val id   = prefs[KEY_USER_ID]      ?: return@map null
        val email = prefs[KEY_EMAIL]        ?: return@map null
        val name  = prefs[KEY_DISPLAY_NAME] ?: return@map null
        val role  = prefs[KEY_ROLE]
            ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
            ?: return@map null
        val verified = prefs[KEY_IS_VERIFIED] ?: false
        AuthUser(id = id, email = email, displayName = name, role = role, isVerified = verified)
    }

    /** Persists the user session after successful login or registration. */
    suspend fun saveSession(user: AuthUser) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID]      = user.id
            prefs[KEY_EMAIL]        = user.email
            prefs[KEY_DISPLAY_NAME] = user.displayName
            prefs[KEY_ROLE]         = user.role.name
            prefs[KEY_IS_VERIFIED]  = user.isVerified
        }
    }

    /** Marks the current session as email-verified. */
    suspend fun markVerified() {
        dataStore.edit { prefs ->
            prefs[KEY_IS_VERIFIED] = true
        }
    }

    /** Clears all session data (logout). */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_DISPLAY_NAME)
            prefs.remove(KEY_ROLE)
            prefs.remove(KEY_IS_VERIFIED)
        }
    }
}
