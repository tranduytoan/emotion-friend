package com.emotionfriend.core.designsystem.theme

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

// ─── LNReader Default Theme - Light Color Scheme ────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = Md3LightPrimary,
    onPrimary            = Md3LightOnPrimary,
    primaryContainer     = Md3LightPrimaryContainer,
    onPrimaryContainer   = Md3LightOnPrimaryContainer,

    secondary            = Md3LightSecondary,
    onSecondary          = Md3LightOnSecondary,
    secondaryContainer   = Md3LightSecondaryContainer,
    onSecondaryContainer = Md3LightOnSecondaryContainer,

    tertiary             = Md3LightTertiary,
    onTertiary           = Md3LightOnTertiary,
    tertiaryContainer    = Md3LightTertiaryContainer,
    onTertiaryContainer  = Md3LightOnTertiaryContainer,

    error                = Md3LightError,
    onError              = Md3LightOnError,
    errorContainer       = Md3LightErrorContainer,
    onErrorContainer     = Md3LightOnErrorContainer,

    background           = Md3LightBackground,
    onBackground         = Md3LightOnBackground,

    surface              = Md3LightSurface,
    onSurface            = Md3LightOnSurface,
    surfaceVariant       = Md3LightSurfaceVariant,
    onSurfaceVariant     = Md3LightOnSurfaceVariant,

    outline              = Md3LightOutline,
)

// ─── LNReader Default Theme - Dark Color Scheme ─────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = Md3DarkPrimary,
    onPrimary            = Md3DarkOnPrimary,
    primaryContainer     = Md3DarkPrimaryContainer,
    onPrimaryContainer   = Md3DarkOnPrimaryContainer,

    secondary            = Md3DarkSecondary,
    onSecondary          = Md3DarkOnSecondary,
    secondaryContainer   = Md3DarkSecondaryContainer,
    onSecondaryContainer = Md3DarkOnSecondaryContainer,

    tertiary             = Md3DarkTertiary,
    onTertiary           = Md3DarkOnTertiary,
    tertiaryContainer    = Md3DarkTertiaryContainer,
    onTertiaryContainer  = Md3DarkOnTertiaryContainer,

    error                = Md3DarkError,
    onError              = Md3DarkOnError,
    errorContainer       = Md3DarkErrorContainer,
    onErrorContainer     = Md3DarkOnErrorContainer,

    background           = Md3DarkBackground,
    onBackground         = Md3DarkOnBackground,

    surface              = Md3DarkSurface,
    onSurface            = Md3DarkOnSurface,
    surfaceVariant       = Md3DarkSurfaceVariant,
    onSurfaceVariant     = Md3DarkOnSurfaceVariant,

    outline              = Md3DarkOutline,
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
