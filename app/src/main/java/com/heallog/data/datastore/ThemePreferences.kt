package com.heallog.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.heallog.model.AppThemeSettings
import com.heallog.model.ColorScheme
import com.heallog.model.FontScale
import com.heallog.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

class ThemePreferences(private val context: Context) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLOR_SCHEME_KEY = stringPreferencesKey("color_scheme")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val FONT_SCALE_KEY = stringPreferencesKey("font_scale")
        private val HIGH_CONTRAST_KEY = booleanPreferencesKey("high_contrast")
    }

    val themeSettings: Flow<AppThemeSettings> = context.themePreferencesDataStore.data.map { preferences ->
        AppThemeSettings(
            themeMode = ThemeMode.valueOf(
                preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ),
            colorScheme = ColorScheme.valueOf(
                preferences[COLOR_SCHEME_KEY] ?: ColorScheme.HEALING_GREEN.name
            ),
            useDynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: false,
            fontScale = FontScale.valueOf(
                preferences[FONT_SCALE_KEY] ?: FontScale.NORMAL.name
            ),
            highContrast = preferences[HIGH_CONTRAST_KEY] ?: false
        )
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.themePreferencesDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    suspend fun updateColorScheme(colorScheme: ColorScheme) {
        context.themePreferencesDataStore.edit { preferences ->
            preferences[COLOR_SCHEME_KEY] = colorScheme.name
        }
    }

    suspend fun updateDynamicColor(useDynamicColor: Boolean) {
        context.themePreferencesDataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = useDynamicColor
        }
    }

    suspend fun updateFontScale(fontScale: FontScale) {
        context.themePreferencesDataStore.edit { preferences ->
            preferences[FONT_SCALE_KEY] = fontScale.name
        }
    }

    suspend fun updateHighContrast(highContrast: Boolean) {
        context.themePreferencesDataStore.edit { preferences ->
            preferences[HIGH_CONTRAST_KEY] = highContrast
        }
    }
}
