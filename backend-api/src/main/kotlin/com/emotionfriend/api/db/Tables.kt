package com.emotionfriend.api.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

// ── Transactional tables — BIGINT AUTO_INCREMENT PKs ─────────────────────────

object JournalEntryTable : Table("journal_entries") {
    val id          = long("id").autoIncrement()
    val childId     = varchar("child_id", 36)
    val emotionType = varchar("emotion_type", 50)
    val note        = text("note").nullable()
    val createdAt   = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object PracticeAttemptTable : Table("practice_attempts") {
    val id            = long("id").autoIncrement()
    val childId       = varchar("child_id", 36)
    val scenarioId    = integer("scenario_id").nullable()
    val isCorrect     = bool("is_correct")
    val promptEmotion = varchar("prompt_emotion", 50).nullable()
    val createdAt     = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

// ── Reference / content tables — INT AUTO_INCREMENT PKs ──────────────────────

object EmotionCardTable : Table("emotion_cards") {
    val id          = integer("id").autoIncrement()
    val emotionType = varchar("emotion_type", 50).uniqueIndex()
    val emoji       = varchar("emoji", 10)
    val label       = varchar("label", 100)
    val description = text("description")
    val sortOrder   = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object LessonTopicTable : Table("lesson_topics") {
    val id          = integer("id").autoIncrement()
    val title       = varchar("title", 200)
    val description = text("description")
    val difficulty  = integer("difficulty")
    val sortOrder   = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object ScenarioLessonTable : Table("scenario_lessons") {
    val id             = integer("id").autoIncrement()
    val title          = varchar("title", 200)
    val situation      = text("situation")
    val options        = text("options")          // JSON array of EmotionType codes
    val correctEmotion = varchar("correct_emotion", 50)
    val explanation    = text("explanation")
    val sortOrder      = integer("sort_order")
    val topicId        = integer("topic_id").nullable()

    override val primaryKey = PrimaryKey(id)
}

object StoryTable : Table("stories") {
    val id        = integer("id").autoIncrement()
    val title     = varchar("title", 200)
    val content   = text("content")
    val category  = varchar("category", 100)
    val imageUrl  = varchar("image_url", 500).nullable()
    val imageFolder = varchar("image_folder", 200).nullable()
    val sortOrder = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}

object AccountTable : Table("accounts") {
    val id         = integer("id").autoIncrement()
    val account    = varchar("account", 255).uniqueIndex()
    val password   = varchar("password", 255)
    val name       = varchar("name", 150)
    val age        = integer("age")
    val avatarUrl  = varchar("avatar_url", 500)
    val createdAt  = timestamp("created_at")
    val updatedAt  = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object MusicTrackTable : Table("music_tracks") {
    val id        = integer("id").autoIncrement()
    val title     = varchar("title", 200)
    val artist    = varchar("artist", 200)
    val filename  = varchar("filename", 300)
    val sortOrder = integer("sort_order")

    override val primaryKey = PrimaryKey(id)
}
