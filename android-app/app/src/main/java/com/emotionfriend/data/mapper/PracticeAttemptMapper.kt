package com.emotionfriend.data.mapper

import com.emotionfriend.data.local.PracticeAttemptEntity
import com.emotionfriend.domain.model.PracticeAttempt

fun PracticeAttemptEntity.toDomain(): PracticeAttempt = PracticeAttempt(
    id              = id,
    childId         = childId,
    taskType        = taskType,
    promptId        = promptId,
    selectedEmotion = selectedEmotion,
    correctEmotion  = correctEmotion,
    isCorrect       = isCorrect,
    createdAt       = createdAt
)

fun PracticeAttempt.toEntity(): PracticeAttemptEntity = PracticeAttemptEntity(
    id              = id,
    childId         = childId,
    taskType        = taskType,
    promptId        = promptId,
    selectedEmotion = selectedEmotion,
    correctEmotion  = correctEmotion,
    isCorrect       = isCorrect,
    createdAt       = createdAt
)
