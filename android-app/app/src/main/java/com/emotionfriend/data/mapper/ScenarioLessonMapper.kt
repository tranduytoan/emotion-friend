package com.emotionfriend.data.mapper

import com.emotionfriend.data.local.ScenarioLessonEntity
import com.emotionfriend.domain.model.ScenarioLesson

fun ScenarioLessonEntity.toDomain(): ScenarioLesson = ScenarioLesson(
    id             = id,
    title          = title,
    situationText  = situationText,
    imageName      = imageName,
    correctEmotion = correctEmotion,
    options        = options,
    explanation    = explanation
)

fun ScenarioLesson.toEntity(): ScenarioLessonEntity = ScenarioLessonEntity(
    id             = id,
    title          = title,
    situationText  = situationText,
    imageName      = imageName,
    correctEmotion = correctEmotion,
    options        = options,
    explanation    = explanation
)
