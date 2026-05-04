package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
enum class EmotionType {
    HAPPY, SAD, ANGRY, SURPRISED, CALM, TIRED
}
