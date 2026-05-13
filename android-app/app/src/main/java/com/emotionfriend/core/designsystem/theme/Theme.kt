package com.emotionfriend.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Dynamic color is intentionally disabled — emotion colors must be stable
// so children always associate the same color with the same emotion.
private val LightColorScheme = lightColorScheme(
    // ── Primary (sky blue — calm, trustworthy) ───────────────────────────────
    primary              = SkyBlue40,
    onPrimary            = OnPrimary,
    primaryContainer     = SkyBlue80,
    onPrimaryContainer   = OnBackground,

    // ── Secondary (mint green — positive, growth) ────────────────────────────
    secondary            = MintGreen40,
    onSecondary          = OnPrimary,
    secondaryContainer   = MintGreen80,
    onSecondaryContainer = OnBackground,

    // ── Tertiary (warm amber — highlights, badges) ────────────────────────────
    tertiary             = SunYellow40,
    onTertiary           = OnPrimary,
    tertiaryContainer    = SunYellow80,
    onTertiaryContainer  = OnBackground,

    // ── Error (soft — avoids alarming children) ───────────────────────────────
    error                = ErrorRed,
    onError              = OnErrorRed,
    errorContainer       = ErrorRedContainer,
    onErrorContainer     = OnErrorRedContainer,

    // ── Background / Surface ─────────────────────────────────────────────────
    background           = WarmCream,
    onBackground         = OnBackground,

    surface              = SurfaceWhite,
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = OnSurfaceVar,

    // ── Inverse (Snackbar, dark overlays) ────────────────────────────────────
    inverseSurface       = SurfaceInverse,
    inverseOnSurface     = OnSurfaceInverse,
    inversePrimary       = PrimaryInverse,

    // ── Outline ──────────────────────────────────────────────────────────────
    outline              = OutlineMedium,
    outlineVariant       = OutlineLight,
)

@Composable
fun EmotionFriendTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppDimensions provides AppDimensions()
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography  = Typography,
            shapes      = Shapes,
            content     = content
        )
    }
}
