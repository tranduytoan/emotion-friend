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
 * Each feature screen receives only the callbacks it needs
 * (navigate-to and navigate-back), not the full NavController.
 * This keeps feature screens decoupled from the navigation library.
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

        composable(AppRoute.Home.route) {
            HomeScreen(
                onNavigateToLearn    = { navController.navigate(AppRoute.LearnEmotion.route) },
                onNavigateToSituation = { navController.navigate(AppRoute.Situation.route) },
                onNavigateToExpress  = { navController.navigate(AppRoute.ExpressCamera.route) },
                onNavigateToRelax    = { navController.navigate(AppRoute.Relax.route) },
                onNavigateToJournal  = { navController.navigate(AppRoute.Journal.route) },
                onNavigateToProgress = { navController.navigate(AppRoute.Progress.route) }
            )
        }

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
