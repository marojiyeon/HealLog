package com.heallog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.heallog.model.AppThemeSettings
import com.heallog.model.ColorScheme
import com.heallog.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTeal80,
    secondary = MedicalGreen80,
    tertiary = SkyBlue80
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTeal40,
    secondary = MedicalGreen40,
    tertiary = SkyBlue40
)

@Composable
fun HealLogTheme(
    themeSettings: AppThemeSettings = AppThemeSettings(),
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        themeSettings.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            val baseScheme = when (themeSettings.colorScheme) {
                ColorScheme.HEALING_GREEN -> if (isDarkTheme) healingGreenDark else healingGreenLight
                ColorScheme.MEDICAL_BLUE -> if (isDarkTheme) medicalBlueDark else medicalBlueLight
                ColorScheme.WARMING_ORANGE -> if (isDarkTheme) warmingOrangeDark else warmingOrangeLight
                ColorScheme.CALM_PURPLE -> if (isDarkTheme) calmPurpleDark else calmPurpleLight
                ColorScheme.DYNAMIC -> if (isDarkTheme) DarkColorScheme else LightColorScheme
            }
            baseScheme
        }
    }

    val typography = appTypography(themeSettings.fontScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
