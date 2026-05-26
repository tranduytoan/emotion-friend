package com.emotionfriend.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.core.designsystem.theme.SurfaceVariant

// ---------------------------------------------------------------------------
// Data model (private, UI-only)
// ---------------------------------------------------------------------------

private data class HomeActivity(
    val emoji: String,
    val label: String,
    val background: Color,
    val onClick: () -> Unit,
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToSituation: () -> Unit,
    onNavigateToExpress: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 4 primary activities — one task per tile, no description text
    val primary = listOf(
        HomeActivity("📚", "Học cảm xúc",   EmotionHappyBg,  onNavigateToLearn),
        HomeActivity("🤝", "Tình huống",    EmotionSadBg,    onNavigateToSituation),
        HomeActivity("📓", "Cảm xúc của con", EmotionCalmBg, onNavigateToJournal),
        HomeActivity("🌈", "Thư giãn",      EmotionAngryBg,  onNavigateToRelax),
    )
    // 2 secondary activities
    val secondary = listOf(
        HomeActivity("🌟", "Tiến trình", MintGreen80, onNavigateToProgress),
        HomeActivity("🧒", "Hồ sơ",     SkyBlueLight, onNavigateToProfile),
    )

    EmotionScreenScaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Greeting ──────────────────────────────────────────────────
            Text(
                text  = "Xin chào! 👋",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text  = "Hôm nay muốn làm gì?",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Primary 2×2 grid ──────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                primary.chunked(2).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { activity ->
                            ActivityTile(
                                activity = activity,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Secondary row ─────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                secondary.forEach { activity ->
                    SecondaryTile(
                        activity = activity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Primary activity tile — large, image-first, no description
// ---------------------------------------------------------------------------

@Composable
private fun ActivityTile(
    activity: HomeActivity,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier
            .heightIn(min = 130.dp)
            .clip(MaterialTheme.shapes.large)
            .background(activity.background)
            .clickable(
                role         = Role.Button,
                onClickLabel = activity.label,
                onClick      = activity.onClick
            )
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text     = activity.emoji,
                fontSize = 52.sp,
                modifier = Modifier.size(72.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text      = activity.label,
                style     = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Secondary tile — smaller, for progress & profile
// ---------------------------------------------------------------------------

@Composable
private fun SecondaryTile(
    activity: HomeActivity,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier
            .heightIn(min = 72.dp)
            .clip(MaterialTheme.shapes.large)
            .background(activity.background)
            .clickable(
                role         = Role.Button,
                onClickLabel = activity.label,
                onClick      = activity.onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = activity.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text  = activity.label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    EmotionFriendTheme {
        HomeScreen(
            onNavigateToLearn     = {},
            onNavigateToSituation = {},
            onNavigateToExpress   = {},
            onNavigateToRelax     = {},
            onNavigateToJournal   = {},
            onNavigateToProgress  = {},
            onNavigateToProfile   = {}
        )
    }
}
