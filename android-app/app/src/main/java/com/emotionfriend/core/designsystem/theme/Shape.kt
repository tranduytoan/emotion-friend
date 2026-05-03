package com.emotionfriend.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Large rounded shapes for a friendly, calm game-learning feel
val Shapes = Shapes(
    // Small chips, badges
    extraSmall = RoundedCornerShape(8.dp),
    // Input fields, snackbars
    small      = RoundedCornerShape(12.dp),
    // Cards, dialogs
    medium     = RoundedCornerShape(20.dp),
    // Bottom sheets, large cards
    large      = RoundedCornerShape(28.dp),
    // Full-pill buttons, FABs
    extraLarge = RoundedCornerShape(50.dp)
)
