package com.emotionfriend.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * App-wide spacing and sizing tokens, designed for child-friendly touch targets.
 *
 * Access via [MaterialTheme.dimensions] inside any `@Composable`:
 * ```kotlin
 * val spacing = MaterialTheme.dimensions.spacingMd   // 16.dp
 * val btnH    = MaterialTheme.dimensions.buttonHeight // 56.dp
 * ```
 *
 * Provided through [LocalAppDimensions] in [EmotionFriendTheme].
 */
@Immutable
data class AppDimensions(

    // ── Spacing scale ─────────────────────────────────────────────────────────
    /** 2dp — hairline gap, rarely used */
    val spacingXxs: Dp = 2.dp,
    /** 4dp — very tight gap between inline elements */
    val spacingXs: Dp = 4.dp,
    /** 8dp — compact gap (e.g. between icon and label) */
    val spacingSm: Dp = 8.dp,
    /** 16dp — default section gap / content padding */
    val spacingMd: Dp = 16.dp,
    /** 24dp — generous gap between cards or sections */
    val spacingLg: Dp = 24.dp,
    /** 32dp — large gap for visual breathing room */
    val spacingXl: Dp = 32.dp,
    /** 48dp — hero / full-screen breathe */
    val spacingXxl: Dp = 48.dp,

    // ── Screen layout ─────────────────────────────────────────────────────────
    /** Horizontal edge padding on all screens (20dp) */
    val screenHorizontalPadding: Dp = 20.dp,
    /** Top/bottom padding inside screen body (16dp) */
    val screenVerticalPadding: Dp = 16.dp,

    // ── Card layout ───────────────────────────────────────────────────────────
    /** Padding inside EmotionCard (20dp) */
    val cardPadding: Dp = 20.dp,
    /** Default card elevation (2dp) */
    val cardElevation: Dp = 2.dp,

    // ── Touch targets (WCAG 2.5.5: min 44×44dp, target 48dp+) ───────────────
    /** Absolute minimum touch target size (48dp) */
    val touchTargetMin: Dp = 48.dp,
    /** Primary action button height (56dp) */
    val buttonHeight: Dp = 56.dp,
    /** Option / choice button height (80dp) */
    val optionButtonHeight: Dp = 80.dp,
    /** Icon button tap area (48dp) */
    val iconButtonSize: Dp = 48.dp,

    // ── Emoji / Illustration sizes ────────────────────────────────────────────
    /** Medium emoji size (40dp) — inline usage */
    val emojiMd: Dp = 40.dp,
    /** Large emoji size (56dp) — card hero */
    val emojiLg: Dp = 56.dp,
    /** Extra-large emoji size (72dp) — full-screen hero */
    val emojiXl: Dp = 72.dp,

    // ── Progress indicators ───────────────────────────────────────────────────
    /** Height of linear progress bars (12dp) */
    val progressBarHeight: Dp = 12.dp,
    /** Diameter of progress pill step dots (8dp / 12dp active) */
    val progressDotSize: Dp = 8.dp,
    val progressDotSizeActive: Dp = 12.dp,
)

/** CompositionLocal carrier for [AppDimensions]. */
val LocalAppDimensions = compositionLocalOf { AppDimensions() }

/**
 * Convenience accessor: `MaterialTheme.dimensions`
 *
 * Example:
 * ```kotlin
 * Modifier.padding(MaterialTheme.dimensions.screenHorizontalPadding)
 * ```
 */
val MaterialTheme.dimensions: AppDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalAppDimensions.current