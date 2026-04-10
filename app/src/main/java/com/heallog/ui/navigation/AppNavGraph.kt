package com.heallog.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heallog.ui.bodymap.BodyMapScreen
import com.heallog.ui.detail.InjuryDetailScreen
import com.heallog.ui.home.HomeScreen
import com.heallog.ui.record.RecordInjuryScreen
import com.heallog.ui.settings.NotificationSettingsScreen
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object BodyMap : Screen

    @Serializable
    data class RecordInjury(val bodyPartId: String, val injuryId: Long = -1L) : Screen

    @Serializable
    data class InjuryDetail(val injuryId: Long) : Screen

    @Serializable
    data object NotificationSettings : Screen
}

private val enterTransition = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
private val exitTransition = slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
private val popEnterTransition = slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn()
private val popExitTransition = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToBodyMap = { navController.navigate(Screen.BodyMap) },
                onNavigateToDetail = { injuryId ->
                    navController.navigate(Screen.InjuryDetail(injuryId))
                },
                onNavigateToNotificationSettings = {
                    navController.navigate(Screen.NotificationSettings)
                }
            )
        }

        composable<Screen.BodyMap> {
            BodyMapScreen(
                onNavigateToRecord = { bodyPartId ->
                    navController.navigate(Screen.RecordInjury(bodyPartId))
                }
            )
        }

        composable<Screen.RecordInjury> {
            RecordInjuryScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Screen.InjuryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.InjuryDetail>()
            InjuryDetailScreen(
                injuryId = route.injuryId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = { bodyPartId ->
                    navController.navigate(
                        Screen.RecordInjury(bodyPartId = bodyPartId, injuryId = route.injuryId)
                    )
                }
            )
        }

        composable<Screen.NotificationSettings> {
            NotificationSettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
