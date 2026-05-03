package com.emotionfriend.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Dynamic color is intentionally disabled — emotion colors must be stable
// so children always associate the same color with the same emotion.
private val LightColorScheme = lightColorScheme(
    primary          = SkyBlue40,
    onPrimary        = OnPrimary,
    primaryContainer = SkyBlue80,
    onPrimaryContainer = OnBackground,

    secondary          = MintGreen40,
    onSecondary        = OnPrimary,
    secondaryContainer = MintGreen80,
    onSecondaryContainer = OnBackground,

    background       = WarmCream,
    onBackground     = OnBackground,

    surface          = SurfaceWhite,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVar,

    outline          = OutlineMedium,
    outlineVariant   = OutlineLight
)

@Composable
fun EmotionFriendTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content
    )
}
