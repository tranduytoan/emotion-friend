package com.emotionfriend.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room schema migrations.
 *
 * v1 → v2: add [syncStatus] and [lastModifiedAt] columns to
 *           journal_entries and practice_attempts for offline-first sync.
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
}
