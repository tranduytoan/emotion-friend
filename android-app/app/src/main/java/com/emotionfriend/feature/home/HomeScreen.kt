package com.emotionfriend.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.SkyBlueLight

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

@OptIn(ExperimentalFoundationApi::class)
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
    val activities = listOf(
        HomeActivity("📚", "Học cảm xúc",     EmotionHappyBg,  onNavigateToLearn),
        HomeActivity("🤝", "Tình huống",       EmotionSadBg,    onNavigateToSituation),
        HomeActivity("📓", "Cảm xúc của con",  EmotionCalmBg,   onNavigateToJournal),
        HomeActivity("🌈", "Thư giãn",         EmotionAngryBg,  onNavigateToRelax),
        HomeActivity("🌟", "Tiến trình",       MintGreen80,     onNavigateToProgress),
        HomeActivity("🧒", "Hồ sơ",            SkyBlueLight,    onNavigateToProfile),
    )

    val pagerState = rememberPagerState(pageCount = { activities.size })
    val tts = rememberTtsPlayer()

    // Read activity name aloud when the page changes
    LaunchedEffect(pagerState.currentPage) {
        tts.speak(activities[pagerState.currentPage].label)
    }

    EmotionScreenScaffold {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Swipe hint ────────────────────────────────────────────────
            Text(
                text  = "Vuốt sang để chọn hoạt động 👉",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )

            // ── Carousel ──────────────────────────────────────────────────
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                ActivityCard(
                    activity = activities[page],
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                )
            }

            // ── Dot indicators ────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                repeat(activities.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Full-screen activity card shown inside the pager
// ---------------------------------------------------------------------------

@Composable
private fun ActivityCard(
    activity: HomeActivity,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier
            .heightIn(min = 320.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(activity.background)
            .clickable(
                role         = Role.Button,
                onClickLabel = activity.label,
                onClick      = activity.onClick,
            )
            .padding(32.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text     = activity.emoji,
                fontSize = 96.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text      = activity.label,
                style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
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
