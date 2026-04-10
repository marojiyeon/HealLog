package com.heallog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.heallog.data.datastore.ThemePreferences
import com.heallog.model.AppThemeSettings
import com.heallog.ui.navigation.AppNavGraph
import com.heallog.ui.theme.HealLogTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSettings = themePreferences.themeSettings.collectAsStateWithLifecycle(
                initialValue = AppThemeSettings()
            )
            HealLogTheme(themeSettings = themeSettings.value) {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
