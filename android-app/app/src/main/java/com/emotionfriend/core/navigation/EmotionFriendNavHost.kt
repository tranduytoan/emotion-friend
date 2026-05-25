package com.emotionfriend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emotionfriend.feature.express.ExpressScreen
import com.emotionfriend.feature.home.HomeScreen
import com.emotionfriend.feature.journal.JournalScreen
import com.emotionfriend.feature.learn.LearnScreen
import com.emotionfriend.feature.progress.ProgressScreen
import com.emotionfriend.feature.relax.RelaxScreen
import com.emotionfriend.feature.situation.SituationScreen

/**
 * Root navigation host for Emotion Friend.
 *
 * Owner: Nghĩa (routing / integration layer only)
 *
 * Architecture decisions:
 * • Feature screens receive only typed callbacks — they are decoupled
 *   from the navigation library entirely.
 * • [navigateSingleTop] prevents a duplicate entry being pushed when the
 *   user double-taps a card on the Home screen.
 * • Back navigation uses [NavHostController.popBackStack]; feature screens
 *   never need to import navigation themselves.
 *
 * Screen ownership:
 * • HomeScreen              — Nghĩa
 * • LearnScreen             — Duy
 * • SituationScreen         — Dũng
 * • ExpressScreen           — Hiệp
 * • RelaxScreen             — Hiệp
 * • JournalScreen           — Toàn
 * • ProgressScreen          — Toàn
 */
@Composable
fun EmotionFriendNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = AppRoute.Home.route,
        modifier         = modifier
    ) {

        // ── Home ─────────────────────────────────────────────────────────────
        composable(AppRoute.Home.route) {
            HomeScreen(
                onNavigateToLearn     = { navController.navigateSingleTop(AppRoute.LearnEmotion.route) },
                onNavigateToSituation = { navController.navigateSingleTop(AppRoute.Situation.route) },
                onNavigateToExpress   = { navController.navigateSingleTop(AppRoute.ExpressCamera.route) },
                onNavigateToRelax     = { navController.navigateSingleTop(AppRoute.Relax.route) },
                onNavigateToJournal   = { navController.navigateSingleTop(AppRoute.Journal.route) },
                onNavigateToProgress  = { navController.navigateSingleTop(AppRoute.Progress.route) }
            )
        }

        // ── Feature screens (UI owned by teammates) ───────────────────────────
        composable(AppRoute.LearnEmotion.route) {
            LearnScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Situation.route) {
            SituationScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.ExpressCamera.route) {
            ExpressScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Relax.route) {
            RelaxScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Journal.route) {
            JournalScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Progress.route) {
            ProgressScreen(onBack = { navController.popBackStack() })
        }
    }
}

// ── Navigation helpers ────────────────────────────────────────────────────────

/**
 * Navigate to [route] with [launchSingleTop] = true so that double-tapping
 * a card never pushes a duplicate entry onto the back stack.
 *
 * Use this for all forward navigation calls from [EmotionFriendNavHost].
 */
private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) { launchSingleTop = true }
}
