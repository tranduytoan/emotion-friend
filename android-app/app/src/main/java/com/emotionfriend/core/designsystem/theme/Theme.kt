package com.emotionfriend.core.designsystem.theme

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

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

// ── Dark Color Scheme (warm dark tone) ──────────────────────────────────
private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary              = PrimaryInverse,
    onPrimary            = OnBackground,
    primaryContainer     = SkyBlue40,
    onPrimaryContainer   = OnPrimary,

    secondary            = MintGreen80,
    onSecondary          = OnBackground,
    secondaryContainer   = MintGreen40,
    onSecondaryContainer = OnPrimary,

    tertiary             = SunYellow80,
    onTertiary           = OnBackground,
    tertiaryContainer    = SunYellow40,
    onTertiaryContainer  = OnPrimary,

    error                = ErrorRed,
    onError              = OnErrorRed,
    errorContainer       = ErrorRedContainer,
    onErrorContainer     = OnErrorRedContainer,

    background           = SurfaceInverse,
    onBackground         = OnSurfaceInverse,

    surface              = Color(0xFF3D3B39), // Slightly lighter than background
    onSurface            = OnSurfaceInverse,
    surfaceVariant       = Color(0xFF4A4846),
    onSurfaceVariant     = Color(0xFFC0BAB0),

    outline              = Color(0xFF807B75),
    outlineVariant       = Color(0xFF5D5B58),
)

@Composable
fun EmotionFriendTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val configuration = LocalConfiguration.current
    val dimensions = remember(configuration) {
        responsiveAppDimensions(configuration)
    }

    CompositionLocalProvider(
        LocalAppDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = Shapes,
            content     = content
        )
    }
}

private fun responsiveAppDimensions(configuration: Configuration): AppDimensions {
    val width = configuration.screenWidthDp
    val height = configuration.screenHeightDp
    val isCompact = width < 360 || height < 640
    val isMedium = width < 420 || height < 720

    return AppDimensions(
        screenHorizontalPadding = when {
            isCompact -> 12.dp
            isMedium -> 16.dp
            else -> 20.dp
        },
        screenVerticalPadding = when {
            isCompact -> 10.dp
            isMedium -> 14.dp
            else -> 16.dp
        },
        cardPadding = when {
            isCompact -> 16.dp
            isMedium -> 18.dp
            else -> 20.dp
        },
        buttonHeight = when {
            isCompact -> 56.dp
            isMedium -> 60.dp
            else -> 64.dp
        },
        optionButtonHeight = when {
            isCompact -> 84.dp
            isMedium -> 92.dp
            else -> 100.dp
        },
        homeTileMinHeight = when {
            isCompact -> 112.dp
            isMedium -> 120.dp
            else -> 130.dp
        },
        iconButtonSize = if (isCompact) 44.dp else 48.dp,
        emojiMd = if (isCompact) 36.dp else 40.dp,
        emojiLg = if (isCompact) 48.dp else 56.dp,
        emojiXl = if (isCompact) 80.dp else 96.dp,
        progressBarHeight = if (isCompact) 10.dp else 12.dp,
        progressDotSize = if (isCompact) 6.dp else 8.dp,
        progressDotSizeActive = if (isCompact) 10.dp else 12.dp,
    )
}
