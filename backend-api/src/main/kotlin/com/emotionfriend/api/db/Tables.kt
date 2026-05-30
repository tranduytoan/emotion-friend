package com.emotionfriend.api.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object JournalEntryTable : Table("journal_entries") {
    val id          = varchar("id", 36)
    val childId     = varchar("child_id", 36)
    val emotionType = varchar("emotion_type", 50)
    val note        = text("note").nullable()
    val createdAt   = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object PracticeAttemptTable : Table("practice_attempts") {
    val id            = varchar("id", 36)
    val childId       = varchar("child_id", 36)
    val scenarioId    = varchar("scenario_id", 50)
    val selectedIndex = integer("selected_index")
    val isCorrect     = bool("is_correct")
    val promptEmotion = varchar("prompt_emotion", 50).nullable()
    val createdAt     = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object EmotionCardTable : Table("emotion_cards") {
    val id          = varchar("id", 50)
    val emotionType = varchar("emotion_type", 50)
    val emoji       = varchar("emoji", 10)
    val label       = varchar("label", 100)
    val description = text("description")
    val sortOrder   = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object ScenarioLessonTable : Table("scenario_lessons") {
    val id           = varchar("id", 50)
    val title        = varchar("title", 200)
    val situation    = text("situation")
    val options      = text("options")          // stored as JSON string
    val correctIndex = integer("correct_index")
    val explanation  = text("explanation")
    val sortOrder    = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object StoryTable : Table("stories") {
    val id        = varchar("id", 36)
    val title     = varchar("title", 200)
    val content   = text("content")
    val category  = varchar("category", 100)
    val imageUrl  = varchar("image_url", 500).nullable()
    val sortOrder = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object MusicTrackTable : Table("music_tracks") {
    val id        = varchar("id", 36)
    val title     = varchar("title", 200)
    val artist    = varchar("artist", 200)
    val filename  = varchar("filename", 300)
    val sortOrder = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}
