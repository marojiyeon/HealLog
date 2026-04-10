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
import com.heallog.ui.detail.hospital.AddMedicationScreen
import com.heallog.ui.detail.hospital.AddVisitScreen
import com.heallog.ui.home.HomeScreen
import com.heallog.ui.record.RecordInjuryScreen
import com.heallog.ui.settings.ThemeSettingsScreen
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object BodyMap : Screen

    @Serializable
    data object ThemeSettings : Screen

    @Serializable
    data class RecordInjury(val bodyPartId: String, val injuryId: Long = -1L) : Screen

    @Serializable
    data class InjuryDetail(val injuryId: Long) : Screen

    @Serializable
    data class AddVisit(val injuryId: Long, val visitId: Long = -1L) : Screen

    @Serializable
    data class AddMedication(val injuryId: Long, val medicationId: Long = -1L) : Screen
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
                onNavigateToSettings = { navController.navigate(Screen.ThemeSettings) }
            )
        }

        composable<Screen.BodyMap> {
            BodyMapScreen(
                onNavigateToRecord = { bodyPartId ->
                    navController.navigate(Screen.RecordInjury(bodyPartId))
                }
            )
        }

        composable<Screen.ThemeSettings> {
            ThemeSettingsScreen(
                onNavigateBack = { navController.navigateUp() }
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
                },
                onNavigateToAddVisit = { injuryId ->
                    navController.navigate(Screen.AddVisit(injuryId))
                },
                onNavigateToEditVisit = { injuryId, visitId ->
                    navController.navigate(Screen.AddVisit(injuryId, visitId))
                },
                onNavigateToAddMedication = { injuryId ->
                    navController.navigate(Screen.AddMedication(injuryId))
                },
                onNavigateToEditMedication = { injuryId, medicationId ->
                    navController.navigate(Screen.AddMedication(injuryId, medicationId))
                }
            )
        }

        composable<Screen.AddVisit> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.AddVisit>()
            AddVisitScreen(
                injuryId = route.injuryId,
                visitId = route.visitId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Screen.AddMedication> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.AddMedication>()
            AddMedicationScreen(
                injuryId = route.injuryId,
                medicationId = route.medicationId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
