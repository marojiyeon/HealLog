package com.heallog.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.toRoute
import com.heallog.ui.bodymap.BodyMapScreen
import com.heallog.ui.detail.InjuryDetailScreen
import com.heallog.ui.home.HomeScreen
import com.heallog.ui.profile.EditProfileScreen
import com.heallog.ui.profile.ProfileScreen
import com.heallog.ui.record.RecordInjuryScreen
import com.heallog.ui.settings.SettingsScreen
import com.heallog.ui.settings.ThemeSettingsScreen
import kotlinx.serialization.Serializable

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
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("홈") }
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
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text("바디맵") }
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
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("내 정보") }
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
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("설정") }
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
            composable<Screen.Home> {
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
        }
    }
}
