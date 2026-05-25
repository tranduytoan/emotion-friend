package com.emotionfriend.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ─── Primary brand ───────────────────────────────────────────────────────────
val SkyBlue40      = Color(0xFF4A90C4)  // primary (calm, friendly)
val SkyBlue80      = Color(0xFFB3D7F2)  // primary container
val SkyBlueLight   = Color(0xFFE8F4FD)  // soft tint background

// ─── Secondary ───────────────────────────────────────────────────────────────
val MintGreen40    = Color(0xFF4CAF82)  // secondary (positive feedback)
val MintGreen80    = Color(0xFFB2DFD0)  // secondary container

// ─── Tertiary (warm accent — badges, highlights, notifications) ───────────────
val SunYellow40    = Color(0xFFF9A825)  // warm amber — non-aggressive highlight
val SunYellow80    = Color(0xFFFFECB3)  // soft amber container

// ─── Background / Surface ────────────────────────────────────────────────────
val WarmCream      = Color(0xFFFFF8F0)  // screen background (warm, not harsh white)
val SurfaceWhite   = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFF4F0EB)  // card background

// ─── Inverse surface (Snackbar / dark overlays) ───────────────────────────────
val SurfaceInverse    = Color(0xFF312F2D)  // warm dark tone (not cold grey)
val OnSurfaceInverse  = Color(0xFFFBEFE8)  // readable warm white on dark
val PrimaryInverse    = Color(0xFF99CDEF)  // lighter sky blue for inverse accent

// ─── On colors ───────────────────────────────────────────────────────────────
val OnPrimary      = Color(0xFFFFFFFF)
val OnBackground   = Color(0xFF2D2D2D)  // legible dark text
val OnSurface      = Color(0xFF3A3A3A)
val OnSurfaceVar   = Color(0xFF6B6B6B)  // secondary text, hints

// ─── Error (soft — avoids harsh pure red for children) ────────────────────────
val ErrorRed            = Color(0xFFBA1A1A)  // M3-aligned error, less harsh than #F44336
val ErrorRedContainer   = Color(0xFFFFDAD6)  // light error container tint
val OnErrorRed          = Color(0xFFFFFFFF)  // text / icons on error fill
val OnErrorRedContainer = Color(0xFF410002)  // text on error container

// ─── Emotion colors (stable — never replaced by dynamic color) ───────────────
val EmotionHappy    = Color(0xFFFFD166)  // yellow-gold  — happy
val EmotionSad      = Color(0xFF74B2D8)  // soft blue     — sad
val EmotionAngry    = Color(0xFFFF8A80)  // soft red-pink — angry (not aggressive)
val EmotionTired    = Color(0xFFB39DDB)  // soft purple   — tired
val EmotionSurprised= Color(0xFFFFCC80)  // warm orange   — surprised
val EmotionCalm     = Color(0xFF80CBC4)  // teal-mint     — calm

// Container tints for emotion chips / option buttons
val EmotionHappyBg    = Color(0xFFFFF8E1)
val EmotionSadBg      = Color(0xFFE3F2FD)
val EmotionAngryBg    = Color(0xFFFFEBEE)
val EmotionTiredBg    = Color(0xFFEDE7F6)
val EmotionSurprisedBg= Color(0xFFFFF3E0)
val EmotionCalmBg     = Color(0xFFE0F2F1)

// ─── Feedback ─────────────────────────────────────────────────────────────────
val FeedbackCorrect   = Color(0xFF4CAF82)  // green
val FeedbackWrong     = Color(0xFFFF8A80)  // soft red
val FeedbackCorrectBg = Color(0xFFE8F5EF)
val FeedbackWrongBg   = Color(0xFFFFEBEE)

// ─── Outline / Divider ────────────────────────────────────────────────────────
val OutlineLight = Color(0xFFDDD8D0)
val OutlineMedium= Color(0xFFC0BAB0)
