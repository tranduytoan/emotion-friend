package com.emotionfriend.repositories

import com.emotionfriend.models.Settings
import javax.sql.DataSource

class SettingsRepository(private val dataSource: DataSource?) {

    private var mockSettings = Settings(
        userId = 1,
        soundEnabled = true,
        notificationEnabled = true,
        reminderTime = "19:00:00",
        language = "vi"
    )

    fun findByUserId(userId: Long): Settings {
        dataSource ?: return mockSettings.copy(userId = userId)
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT sound_enabled, notification_enabled, reminder_time, language " +
                    "FROM settings WHERE user_id = ?"
                ).apply { setLong(1, userId) }.executeQuery().let { rs ->
                    if (rs.next()) {
                        Settings(
                            userId = userId,
                            soundEnabled = rs.getBoolean("sound_enabled"),
                            notificationEnabled = rs.getBoolean("notification_enabled"),
                            reminderTime = rs.getString("reminder_time"),
                            language = rs.getString("language")
                        )
                    } else {
                        mockSettings.copy(userId = userId)
                    }
                }
            }
        } catch (e: Exception) {
            mockSettings.copy(userId = userId)
        }
    }

    fun update(
        userId: Long,
        soundEnabled: Boolean?,
        notificationEnabled: Boolean?,
        reminderTime: String?,
        language: String?
    ): Settings {
        dataSource ?: return updateMock(userId, soundEnabled, notificationEnabled, reminderTime, language)
        return try {
            val current = findByUserId(userId)
            val next = current.copy(
                soundEnabled = soundEnabled ?: current.soundEnabled,
                notificationEnabled = notificationEnabled ?: current.notificationEnabled,
                reminderTime = reminderTime ?: current.reminderTime,
                language = language ?: current.language
            )
            dataSource.connection.use { conn ->
                val updated = conn.prepareStatement(
                    "UPDATE settings SET sound_enabled = ?, notification_enabled = ?, reminder_time = ?, language = ? " +
                    "WHERE user_id = ?"
                ).apply {
                    setBoolean(1, next.soundEnabled)
                    setBoolean(2, next.notificationEnabled)
                    setString(3, next.reminderTime)
                    setString(4, next.language)
                    setLong(5, userId)
                }.executeUpdate()

                if (updated == 0) {
                    conn.prepareStatement(
                        "INSERT INTO settings (user_id, sound_enabled, notification_enabled, reminder_time, language) " +
                        "VALUES (?, ?, ?, ?, ?)"
                    ).apply {
                        setLong(1, userId)
                        setBoolean(2, next.soundEnabled)
                        setBoolean(3, next.notificationEnabled)
                        setString(4, next.reminderTime)
                        setString(5, next.language)
                    }.executeUpdate()
                }
            }
            next
        } catch (e: Exception) {
            updateMock(userId, soundEnabled, notificationEnabled, reminderTime, language)
        }
    }

    private fun updateMock(
        userId: Long,
        soundEnabled: Boolean?,
        notificationEnabled: Boolean?,
        reminderTime: String?,
        language: String?
    ): Settings {
        val current = mockSettings.copy(userId = userId)
        val next = current.copy(
            soundEnabled = soundEnabled ?: current.soundEnabled,
            notificationEnabled = notificationEnabled ?: current.notificationEnabled,
            reminderTime = reminderTime ?: current.reminderTime,
            language = language ?: current.language
        )
        mockSettings = next
        return next
    }
}
