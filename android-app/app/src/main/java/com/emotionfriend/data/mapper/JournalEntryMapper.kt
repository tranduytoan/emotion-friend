package com.emotionfriend.data.mapper

import com.emotionfriend.data.local.JournalEntryEntity
import com.emotionfriend.domain.model.JournalEntry

fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id          = id,
    childId     = childId,
    emotionType = emotionType,
    note        = note,
    createdAt   = createdAt,
    audioPath   = audioPath,
)

fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id          = id,
    childId     = childId,
    emotionType = emotionType,
    note        = note,
    createdAt   = createdAt,
    audioPath   = audioPath,
)
