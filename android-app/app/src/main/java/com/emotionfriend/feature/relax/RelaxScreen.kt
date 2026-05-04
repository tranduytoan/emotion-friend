package com.emotionfriend.feature.relax

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight

// ---------------------------------------------------------------------------
// Activity selection state
// ---------------------------------------------------------------------------

private enum class RelaxActivity { NONE, MUSIC, BREATHING, PUZZLE }

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun RelaxScreen(
    onBack  : () -> Unit,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(RelaxActivity.NONE) }

    EmotionScreenScaffold(title = "Thời gian thư giãn", onBack = onBack) {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text      = "Chọn một hoạt động nhẹ nhàng để bình tĩnh lại.",
                style     = MaterialTheme.typography.bodyLarge,
                color     = OnSurfaceVar,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            // ── 1. Nghe nhạc nhẹ ─────────────────────────────────────────
            ActivityCard(
                emoji      = "🎵",
                title      = "Nghe nhạc nhẹ",
                background = EmotionHappyBg,
                accentColor= EmotionHappy,
                isActive   = active == RelaxActivity.MUSIC,
                onToggle   = {
                    active = if (active == RelaxActivity.MUSIC) RelaxActivity.NONE
                              else RelaxActivity.MUSIC
                }
            ) {
                if (active == RelaxActivity.MUSIC) {
                    MusicContent()
                }
            }

            // ── 2. Thở cùng bóng ──────────────────────────────────────────
            ActivityCard(
                emoji      = "🫧",
                title      = "Thở cùng bóng",
                background = EmotionCalmBg,
                accentColor= EmotionCalm,
                isActive   = active == RelaxActivity.BREATHING,
                onToggle   = {
                    active = if (active == RelaxActivity.BREATHING) RelaxActivity.NONE
                              else RelaxActivity.BREATHING
                }
            ) {
                if (active == RelaxActivity.BREATHING) {
                    BreathingContent()
                }
            }

            // ── 3. Trò chơi xếp hình ─────────────────────────────────────
            ActivityCard(
                emoji      = "🧩",
                title      = "Trò chơi xếp hình đơn giản",
                background = SkyBlueLight,
                accentColor= SkyBlue40,
                isActive   = active == RelaxActivity.PUZZLE,
                onToggle   = {
                    active = if (active == RelaxActivity.PUZZLE) RelaxActivity.NONE
                              else RelaxActivity.PUZZLE
                }
            ) {
                if (active == RelaxActivity.PUZZLE) {
                    PuzzleContent()
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable activity card shell
// ---------------------------------------------------------------------------

@Composable
private fun ActivityCard(
    emoji      : String,
    title      : String,
    background : Color,
    accentColor: Color,
    isActive   : Boolean,
    onToggle   : () -> Unit,
    content    : @Composable () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isActive) background else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(400),
        label = "cardBg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(animatedBg)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text     = title,
                    style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }
            EmotionPrimaryButton(
                text    = if (isActive) "Dừng lại" else "Bắt đầu",
                onClick = onToggle
            )
            content()
        }
    }
}

// ---------------------------------------------------------------------------
// Activity content panes
// ---------------------------------------------------------------------------

/** Mock music player — no audio API, purely informational text. */
@Composable
private fun MusicContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "musicPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue   = 0.5f,
        targetValue    = 1f,
        animationSpec  = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label          = "alpha"
    )
    EmotionCard {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text  = "♪",
                style = MaterialTheme.typography.headlineMedium,
                color = MintGreen40.copy(alpha = alpha)
            )
            Column {
                Text(
                    text  = "Đang phát nhạc nhẹ...",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text  = "Hãy nhắm mắt và thở đều nhé 😌",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVar
                )
            }
        }
    }
}

/** Breathing bubble — an infinite expand/contract circle with guiding text. */
@Composable
private fun BreathingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 0.75f,
        targetValue   = 1.25f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label         = "scale"
    )
    val phaseText = if (scale < 1f) "Hít vào..." else "Thở ra..."

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier            = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(EmotionCalm.copy(alpha = 0.35f))
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(EmotionCalm.copy(alpha = 0.7f))
            )
        }
        Text(
            text      = phaseText,
            style     = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
            color     = EmotionCalm,
            textAlign = TextAlign.Center
        )
        Text(
            text      = "Thở theo nhịp bóng — hít vào 3 giây, thở ra 3 giây.",
            style     = MaterialTheme.typography.bodySmall,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )
    }
}

/** Puzzle placeholder — no game engine needed at MVP. */
@Composable
private fun PuzzleContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.fillMaxWidth()
    ) {
        // Simple 2×2 static tile grid as visual placeholder
        val tileColors = listOf(
            listOf(MintGreen80, SkyBlueLight),
            listOf(SkyBlueLight, MintGreen80)
        )
        tileColors.forEach { rowColors ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(color)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text      = "Sắp xếp hình đơn giản",
            style     = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text      = "Tính năng trò chơi xếp hình sẽ sớm ra mắt! 🧩",
            style     = MaterialTheme.typography.bodySmall,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RelaxScreenPreview() {
    EmotionFriendTheme {
        RelaxScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BreathingContentPreview() {
    EmotionFriendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BreathingContent()
        }
    }
}

