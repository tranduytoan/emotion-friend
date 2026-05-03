package com.emotionfriend.data.mapper

import com.emotionfriend.data.local.EmotionCardEntity
import com.emotionfriend.domain.model.EmotionCard

fun EmotionCardEntity.toDomain(): EmotionCard = EmotionCard(
    id          = id,
    label       = label,
    emoji       = emoji,
    type        = type,
    description = description
)

fun EmotionCard.toEntity(): EmotionCardEntity = EmotionCardEntity(
    id          = id,
    label       = label,
    emoji       = emoji,
    type        = type,
    description = description
)
