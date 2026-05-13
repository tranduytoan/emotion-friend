package com.emotionfriend.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Child-friendly typography: generous sizes, high legibility, minimal letter-spacing.
// All M3 slots are defined so features never fall back to unexpected Material defaults.
val Typography = Typography(

    // ── Display (hero text, full-screen prompts) ──────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 40.sp,
        lineHeight    = 48.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 34.sp,
        lineHeight    = 42.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 30.sp,
        lineHeight    = 38.sp,
        letterSpacing = 0.sp
    ),

    // ── Headlines (section headings) ─────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 32.sp,
        lineHeight    = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 26.sp,
        lineHeight    = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 22.sp,
        lineHeight    = 30.sp,
        letterSpacing = 0.sp
    ),

    // ── Titles (card headings, TopAppBar, dialog titles) ─────────────────────
    titleLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 22.sp,
        lineHeight    = 30.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Medium,
        fontSize      = 18.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Medium,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp
    ),

    // ── Body (readable content — kept short in this app) ─────────────────────
    bodyLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 18.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.2.sp
    ),

    // ── Labels (button text, chips, captions) ────────────────────────────────
    labelLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 18.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Medium,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.1.sp
    )
)
