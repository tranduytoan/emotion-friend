package com.emotionfriend.repositories

import com.emotionfriend.models.Situation
import javax.sql.DataSource

class SituationRepository(private val dataSource: DataSource?) {

    fun findAll(): List<Situation> {
        dataSource ?: return mockData()
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT id, title, description, emotion_id, image_url, difficulty_level " +
                    "FROM situations ORDER BY id"
                ).executeQuery().let { rs ->
                    buildList {
                        while (rs.next()) add(
                            Situation(
                                id              = rs.getInt("id"),
                                title           = rs.getString("title"),
                                description     = rs.getString("description"),
                                emotionId       = rs.getInt("emotion_id"),
                                imageUrl        = rs.getString("image_url"),
                                difficultyLevel = rs.getString("difficulty_level")
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) { mockData() }
    }

    private fun mockData() = listOf(
        Situation(1,  "Được tặng quà sinh nhật",       null, 1, null, "easy"),
        Situation(2,  "Chơi đu quay cùng bạn",         null, 1, null, "easy"),
        Situation(3,  "Được khen trước lớp",            null, 1, null, "easy"),
        Situation(4,  "Mất đồ chơi yêu thích",         null, 2, null, "easy"),
        Situation(5,  "Bạn bè không chịu chơi cùng",   null, 2, null, "easy"),
        Situation(6,  "Em lấy đồ chơi không hỏi",      null, 3, null, "easy"),
        Situation(7,  "Bị đổ lỗi oan",                 null, 3, null, "easy"),
        Situation(8,  "Học bài đến khuya",              null, 4, null, "easy"),
        Situation(9,  "Bạn bè tổ chức tiệc bất ngờ",   null, 5, null, "easy"),
        Situation(10, "Ngồi nghe nhạc trong phòng",     null, 6, null, "easy")
    )
}
