package com.emotionfriend.repositories

import com.emotionfriend.models.Emotion
import javax.sql.DataSource

class EmotionRepository(private val dataSource: DataSource?) {

    fun findAll(): List<Emotion> {
        dataSource ?: return mockData()
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT id, name, display_name, emoji, color_hex, description FROM emotions ORDER BY id"
                ).executeQuery().let { rs ->
                    buildList {
                        while (rs.next()) add(
                            Emotion(
                                id          = rs.getInt("id"),
                                name        = rs.getString("name"),
                                displayName = rs.getString("display_name"),
                                emoji       = rs.getString("emoji"),
                                colorHex    = rs.getString("color_hex"),
                                description = rs.getString("description")
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) { mockData() }
    }

    // Mock data matches seed in database/schema.sql and Color.kt tokens
    private fun mockData() = listOf(
        Emotion(1, "happy",     "Vui",       "\uD83D\uDE04", "#FFD600", "Cảm giác hạnh phúc, vui vẻ"),
        Emotion(2, "sad",       "Buồn",      "\uD83D\uDE22", "#42A5F5", "Cảm giác buồn bã, không vui"),
        Emotion(3, "angry",     "Tức giận",  "\uD83D\uDE20", "#EF5350", "Cảm giác tức giận, bực bội"),
        Emotion(4, "tired",     "Mệt mỏi",   "\uD83D\uDE34", "#AB47BC", "Cảm giác mệt mỏi, buồn ngủ"),
        Emotion(5, "surprised", "Bất ngờ",   "\uD83D\uDE32", "#FF7043", "Cảm giác ngạc nhiên, bất ngờ"),
        Emotion(6, "calm",      "Bình tĩnh", "\uD83D\uDE0C", "#66BB6A", "Cảm giác bình tĩnh, thư giãn")
    )
}
