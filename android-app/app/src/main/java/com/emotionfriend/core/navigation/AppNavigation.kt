package com.emotionfriend.core.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = AppDestinations.HOME
    ) {
        composable(AppDestinations.HOME)      { HomeScreen(navController = navController) }
        composable(AppDestinations.LEARN)     { LearnScreen() }
        composable(AppDestinations.SITUATION) { SituationScreen() }
        composable(AppDestinations.EXPRESS)   { ExpressScreen() }
        composable(AppDestinations.RELAX)     { RelaxScreen() }
        composable(AppDestinations.JOURNAL)   { JournalScreen() }
        composable(AppDestinations.PROGRESS)  { ProgressScreen() }
    }
}
