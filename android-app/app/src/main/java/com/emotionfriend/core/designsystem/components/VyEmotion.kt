package com.emotionfriend.core.designsystem.components

import com.emotionfriend.domain.model.EmotionType

/**
 * Emotional states of "Cô giáo Vy" that drive her avatar expression.
 *
 * Each state maps to a distinct image resource file (res/drawable/vy_<name>.png).
 * Until the designer provides the images the code falls back to emoji placeholders.
 *
 * Image naming convention:
 *   vy_neutral.png       — default
 *   vy_excited.png       — enthusiastic welcome
 *   vy_happy.png         — correct answer / celebration
 *   vy_encouraging.png   — wrong answer, comforting
 *   vy_calm.png          — relaxation activities
 *   vy_celebrating.png   — lesson set completed
 */
enum class VyEmotion {
    NEUTRAL,
    EXCITED,
    HAPPY,
    ENCOURAGING,
    CALM,
    CELEBRATING,
}

internal fun EmotionType.toVyEmotionForCompanion(): VyEmotion = when (this) {
    EmotionType.HAPPY     -> VyEmotion.HAPPY
    EmotionType.SAD       -> VyEmotion.ENCOURAGING
    EmotionType.ANGRY     -> VyEmotion.CALM
    EmotionType.SURPRISED -> VyEmotion.EXCITED
    EmotionType.CALM      -> VyEmotion.CALM
    EmotionType.TIRED     -> VyEmotion.ENCOURAGING
}
