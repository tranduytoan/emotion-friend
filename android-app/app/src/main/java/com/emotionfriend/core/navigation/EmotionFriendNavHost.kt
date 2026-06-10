package com.emotionfriend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emotionfriend.domain.model.AuthState
import com.emotionfriend.feature.auth.AuthViewModel
import com.emotionfriend.feature.auth.ForgotPasswordScreen
import com.emotionfriend.feature.auth.LoginScreen
import com.emotionfriend.feature.auth.RegisterScreen
import com.emotionfriend.feature.auth.VerifyEmailScreen
import com.emotionfriend.feature.express.ExpressScreen
import com.emotionfriend.feature.home.HomeScreen
import com.emotionfriend.feature.journal.DailyCheckInScreen
import com.emotionfriend.feature.journal.JournalScreen
import com.emotionfriend.feature.learn.LearnScreen
import com.emotionfriend.feature.profile.ProfileScreen
import com.emotionfriend.feature.progress.ProgressScreen
import com.emotionfriend.feature.relax.RelaxScreen
import com.emotionfriend.feature.story.StoryScreen
import com.emotionfriend.feature.confide.ConfideScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Root navigation host for Emotion Friend.
 *
 * Owner: Nghĩa (routing / integration layer only)
 *
 * Architecture decisions:
 * • The auth state is observed here to determine the [NavHost.startDestination].
 *   While [AuthState.Loading], we keep the start destination as Login so the
 *   splash shows until the DataStore check resolves.
 * • After a successful login/register the graph pops the entire auth back-stack
 *   so the user cannot navigate back to auth screens with the system Back button.
 * • All roles land on HomeScreen after login.
 * • Feature screens receive only typed callbacks — decoupled from navigation.
 *
 * Screen ownership:
 * • LoginScreen, RegisterScreen, ForgotPasswordScreen, VerifyEmailScreen — Nghĩa
 * • HomeScreen              — Nghĩa
 * • LearnScreen             — Duy
 * • ExpressScreen           — Hiệp
 * • RelaxScreen             — Hiệp
 * • JournalScreen           — Toàn
 * • ProgressScreen          — Toàn
 */
@Composable
fun EmotionFriendNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // Hilt ViewModel is scoped to the NavHost back-stack entry, so it survives
    // configuration changes but is cleared when the user fully exits the auth flow.
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Determine initial destination from persisted session.
    val startDestination = when (authState) {
        is AuthState.Authenticated -> AppRoute.Home.route
        else -> AppRoute.Login.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        AppRoute.Home.route,
        AppRoute.Journal.route,
        AppRoute.Progress.route,
        AppRoute.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                EmotionFriendBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = modifier.padding(innerPadding),
        ) {

        // ── Auth screens ──────────────────────────────────────────────────────

        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel              = authViewModel,
                onLoginSuccess         = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister       = { navController.navigateSingleTop(AppRoute.Register.route) },
                onNavigateToForgotPassword = { navController.navigateSingleTop(AppRoute.ForgotPassword.route) },
            )
        }

        composable(AppRoute.Register.route) {
            RegisterScreen(
                viewModel              = authViewModel,
                onAwaitingVerification = { email ->
                    navController.navigate(AppRoute.VerifyEmail.createRoute(email)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(AppRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel       = authViewModel,
                onNavigateBack  = { navController.popBackStack() },
            )
        }

        composable(
            route     = AppRoute.VerifyEmail.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyEmailScreen(
                email           = email,
                viewModel       = authViewModel,
                onVerifySuccess = { _ ->
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBack  = { navController.popBackStack() },
            )
        }

        // ── Daily check-in (first screen for CHILD) ──────────────────────────

        composable(AppRoute.DailyCheckIn.route) {
            DailyCheckInScreen(
                onComplete = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.DailyCheckIn.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Home ─────────────────────────────────────────────────────────────

        composable(AppRoute.Home.route) {
            val userName = (authState as? AuthState.Authenticated)?.user?.displayName ?: ""
            HomeScreen(
                onNavigateToLearn     = { navController.navigateSingleTop(AppRoute.LearnEmotion.route) },
                onNavigateToSituation = { navController.navigateSingleTop(AppRoute.LearnEmotion.route) },
                onNavigateToExpress   = { navController.navigateSingleTop(AppRoute.ExpressCamera.route) },
                onNavigateToRelax     = { navController.navigateSingleTop(AppRoute.Relax.route) },
                onNavigateToJournal   = { navController.navigateSingleTop(AppRoute.Journal.route) },
                onNavigateToProgress  = { navController.navigateSingleTop(AppRoute.Progress.route) },
                onNavigateToProfile   = { navController.navigateSingleTop(AppRoute.Profile.route) },
                onNavigateToStory     = { navController.navigateSingleTop(AppRoute.Story.route) },
                onNavigateToConfide   = { navController.navigateSingleTop(AppRoute.Confide.route) },
                userName              = userName,
            )
        }

        // ── Feature screens (UI owned by teammates) ───────────────────────────

        composable(AppRoute.LearnEmotion.route) {
            LearnScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Situation.route) {
            // Situation is now merged into LearnScreen
            androidx.compose.runtime.LaunchedEffect(Unit) {
                navController.navigate(AppRoute.LearnEmotion.route) {
                    popUpTo(AppRoute.Situation.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
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

        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onBack   = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppRoute.Story.route) {
            StoryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(AppRoute.Confide.route) {
            ConfideScreen(
                onNavigateBack    = { navController.popBackStack() },
                onNavigateToStory = { navController.navigateSingleTop(AppRoute.Story.route) },
                onNavigateToRelax = { navController.navigateSingleTop(AppRoute.Relax.route) },
            )
        }
    }
}
}

// ── Navigation helpers ────────────────────────────────────────────────────────

/**
 * Navigate to [route] with [launchSingleTop] = true so that double-tapping
 * never pushes a duplicate entry onto the back stack.
 */
private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) { launchSingleTop = true }
}
