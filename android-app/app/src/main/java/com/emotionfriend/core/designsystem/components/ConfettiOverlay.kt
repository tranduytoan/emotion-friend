package com.emotionfriend.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val xFraction: Float,
    val yStartFraction: Float,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val oscillation: Float,
)

private val confettiColors = listOf(
    Color(0xFFFFD700), // gold
    Color(0xFFFF6B6B), // coral
    Color(0xFF74C7EC), // sky
    Color(0xFFA8E6CF), // mint
    Color(0xFFFFB347), // orange
    Color(0xFFDDA0DD), // plum
    Color(0xFF98FB98), // pale green
    Color(0xFFFF69B4), // hot pink
)

/**
 * Full-screen confetti burst overlay.
 * Renders only when [active] is true; stops (and is removed from composition) when false.
 * Does NOT consume touch events — the content below remains fully interactive.
 *
 * Usage: place inside a [Box] that fills the desired area, above the screen content:
 * ```
 * Box(Modifier.fillMaxSize()) {
 *     ScreenContent(...)
 *     ConfettiOverlay(active = isCorrect, modifier = Modifier.fillMaxSize())
 * }
 * ```
 */
@Composable
fun ConfettiOverlay(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!active) return

    val particles = remember {
        List(70) {
            ConfettiParticle(
                xFraction      = Random.nextFloat(),
                yStartFraction = -0.05f - Random.nextFloat() * 0.6f,
                speed          = 0.18f + Random.nextFloat() * 0.28f,
                radius         = 6f + Random.nextFloat() * 10f,
                color          = confettiColors.random(),
                oscillation    = Random.nextFloat() * 6.28f,
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val t by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_000, easing = LinearEasing),
        ),
        label = "confettiTime",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        particles.forEach { p ->
            val progress = p.yStartFraction + t * p.speed * 4f
            if (progress < 0f || progress > 1.3f) return@forEach
            val px = p.xFraction * w + 40f * sin(t * 6.28f * 2 + p.oscillation)
            val py = progress * h
            val alpha = (1f - progress / 1.3f).coerceIn(0f, 1f)
            drawCircle(
                color  = p.color,
                radius = p.radius,
                center = Offset(px.coerceIn(0f, w), py.coerceIn(0f, h)),
                alpha  = alpha,
            )
        }
    }
}
