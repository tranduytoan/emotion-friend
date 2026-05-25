package com.emotionfriend.repositories

import com.emotionfriend.models.Progress
import javax.sql.DataSource

class ProgressRepository(private val dataSource: DataSource?) {

    fun findByUserId(userId: Long): Progress {
        dataSource ?: return mockData(userId)
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT total_exercises, correct_answers, current_streak, longest_streak, last_activity_date " +
                    "FROM progress WHERE user_id = ?"
                ).apply { setLong(1, userId) }.executeQuery().let { rs ->
                    if (rs.next()) {
                        Progress(
                            userId           = userId,
                            totalExercises   = rs.getInt("total_exercises"),
                            correctAnswers   = rs.getInt("correct_answers"),
                            currentStreak    = rs.getInt("current_streak"),
                            longestStreak    = rs.getInt("longest_streak"),
                            lastActivityDate = rs.getString("last_activity_date")
                        )
                    } else mockData(userId)
                }
            }
        } catch (e: Exception) { mockData(userId) }
    }

    private fun mockData(userId: Long) = Progress(
        userId           = userId,
        totalExercises   = 20,
        correctAnswers   = 14,
        currentStreak    = 3,
        longestStreak    = 5,
        lastActivityDate = "2026-05-13"
    )
}
