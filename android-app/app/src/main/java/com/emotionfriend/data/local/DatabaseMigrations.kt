package com.emotionfriend.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room schema migrations.
 *
 * v1 → v2: add [syncStatus] and [lastModifiedAt] columns to
 *           journal_entries and practice_attempts for offline-first sync.
 * v2 → v3: add [audioPath] column to journal_entries for voice recordings.
 * v3 → v4: add [stories] table for the Story feature.
 */
object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE journal_entries ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING'"
            )
            database.execSQL(
                "ALTER TABLE journal_entries ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0"
            )
            database.execSQL(
                "ALTER TABLE practice_attempts ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING'"
            )
            database.execSQL(
                "ALTER TABLE practice_attempts ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE journal_entries ADD COLUMN audioPath TEXT"
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS stories (
                    id       TEXT NOT NULL PRIMARY KEY,
                    title    TEXT NOT NULL,
                    content  TEXT NOT NULL,
                    image1   TEXT NOT NULL DEFAULT '',
                    image2   TEXT NOT NULL DEFAULT '',
                    image3   TEXT NOT NULL DEFAULT '',
                    image4   TEXT NOT NULL DEFAULT '',
                    category TEXT NOT NULL DEFAULT 'DEFAULT'
                )
                """.trimIndent()
            )
        }
    }
}
