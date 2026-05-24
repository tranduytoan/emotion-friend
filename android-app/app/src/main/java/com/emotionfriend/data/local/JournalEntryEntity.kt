package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emotionfriend.domain.model.EmotionType

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val childId: String,
    val emotionType: EmotionType,
    val note: String?,
    val createdAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
