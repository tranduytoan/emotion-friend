package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_topics")
data class LessonTopicEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String = "",
    val difficulty: Int = 1,
    val sortOrder: Int = 0,
)
