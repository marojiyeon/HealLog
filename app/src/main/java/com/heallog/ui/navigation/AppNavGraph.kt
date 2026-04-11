package com.heallog.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.heallog.ui.bodymap.BodyMapScreen
import com.heallog.ui.detail.InjuryDetailScreen
import com.heallog.ui.detail.hospital.AddMedicationScreen
import com.heallog.ui.detail.hospital.AddVisitScreen
import com.heallog.ui.home.HomeScreen
import com.heallog.ui.profile.EditProfileScreen
import com.heallog.ui.profile.ProfileScreen
import com.heallog.ui.record.RecordInjuryScreen
import com.heallog.ui.settings.DataExportScreen
import com.heallog.ui.settings.NotificationSettingsScreen
import com.heallog.ui.settings.PrivacyPolicyScreen
import com.heallog.ui.settings.SettingsScreen
import com.heallog.ui.settings.ThemeSettingsScreen
import kotlinx.serialization.Serializable
import androidx.compose.ui.res.stringResource
import com.heallog.R

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object BodyMap : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object EditProfile : Screen

    @Serializable
    data object ThemeSettings : Screen

    @Serializable
    data class RecordInjury(val bodyPartId: String, val injuryId: Long = -1L) : Screen

    @Serializable
    data class InjuryDetail(val injuryId: Long) : Screen

    @Serializable
    data object NotificationSettings : Screen

    @Serializable
    data object DataExport : Screen

    @Serializable
    data object PrivacyPolicy : Screen

    @Serializable
    data class AddVisit(val injuryId: Long, val visitId: Long = -1L) : Screen

    @Serializable
    data class AddMedication(val injuryId: Long, val medicationId: Long = -1L) : Screen
}

private val TOP_LEVEL_ROUTES: List<Screen> = listOf(
    Screen.Home,
    Screen.BodyMap,
    Screen.Profile,
    Screen.Settings
)

private val enterTransition = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
private val exitTransition = slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
private val popEnterTransition = slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn()
private val popExitTransition = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()

@Composable
fun AppNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = TOP_LEVEL_ROUTES.any { route ->
        currentDestination?.hasRoute(route::class) == true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Screen.Home::class) == true,
                        onClick = {
                            navController.navigate(Screen.Home) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Screen.BodyMap::class) == true,
                        onClick = {
                            navController.navigate(Screen.BodyMap) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.AccessibilityNew, contentDescription = stringResource(R.string.nav_bodymap)) },
                        label = { Text(stringResource(R.string.nav_bodymap)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Screen.Profile::class) == true,
                        onClick = {
                            navController.navigate(Screen.Profile) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.nav_profile)) },
                        label = { Text(stringResource(R.string.nav_profile)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Screen.Settings::class) == true,
                        onClick = {
                            navController.navigate(Screen.Settings) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings)) },
                        label = { Text(stringResource(R.string.nav_settings)) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            composable<Screen.Home>(
                deepLinks = listOf(navDeepLink { uriPattern = "heallog://home" })
            ) {
                HomeScreen(
                    onNavigateToBodyMap = {
                        navController.navigate(Screen.BodyMap) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDetail = { injuryId ->
                        navController.navigate(Screen.InjuryDetail(injuryId))
                    },
                    onNavigateToNotificationSettings = {
                        navController.navigate(Screen.NotificationSettings)
                    }
                )
            }

            composable<Screen.BodyMap>(
                deepLinks = listOf(navDeepLink { uriPattern = "heallog://bodymap" })
            ) {
                BodyMapScreen(
                    onNavigateToRecord = { bodyPartId ->
                        navController.navigate(Screen.RecordInjury(bodyPartId))
                    }
                )
            }

            composable<Screen.Profile> {
                ProfileScreen(
                    onNavigateToEdit = { navController.navigate(Screen.EditProfile) }
                )
            }

            composable<Screen.EditProfile> {
                EditProfileScreen(
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateToThemeSettings = { navController.navigate(Screen.ThemeSettings) },
                    onNavigateToNotificationSettings = { navController.navigate(Screen.NotificationSettings) },
                    onNavigateToDataExport = { navController.navigate(Screen.DataExport) },
                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy) },
                    onBack = { navController.navigateUp() }
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

            composable<Screen.InjuryDetail>(
                deepLinks = listOf(navDeepLink { uriPattern = "heallog://injury/{injuryId}" })
            ) { backStackEntry ->
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

            composable<Screen.NotificationSettings> {
                NotificationSettingsScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<Screen.DataExport> {
                DataExportScreen(onBack = { navController.navigateUp() })
            }

            composable<Screen.PrivacyPolicy> {
                PrivacyPolicyScreen(onBack = { navController.navigateUp() })
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
}
