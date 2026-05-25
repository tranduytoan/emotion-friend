package com.emotionfriend.repositories

import com.emotionfriend.models.EmotionLog
import java.sql.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

class EmotionLogRepository(private val dataSource: DataSource?) {

    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun insert(userId: Long, emotionId: Int, note: String?): EmotionLog {
        dataSource ?: return mockInsert(userId, emotionId, note)
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "INSERT INTO emotion_logs (user_id, emotion_id, note) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                ).apply {
                    setLong(1, userId)
                    setInt(2, emotionId)
                    setString(3, note)
                }.also { it.executeUpdate() }.generatedKeys.let { keys ->
                    val id = if (keys.next()) keys.getLong(1) else 0L
                    EmotionLog(
                        id        = id,
                        userId    = userId,
                        emotionId = emotionId,
                        note      = note,
                        loggedAt  = LocalDateTime.now().format(fmt)
                    )
                }
            }
        } catch (e: Exception) { mockInsert(userId, emotionId, note) }
    }

    private fun mockInsert(userId: Long, emotionId: Int, note: String?) = EmotionLog(
        id        = System.currentTimeMillis(),
        userId    = userId,
        emotionId = emotionId,
        note      = note,
        loggedAt  = LocalDateTime.now().format(fmt)
    )
}
