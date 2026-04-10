package com.heallog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.datastore.ThemePreferences
import com.heallog.model.AppThemeSettings
import com.heallog.model.ColorScheme
import com.heallog.model.FontScale
import com.heallog.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {
    val themeSettings: StateFlow<AppThemeSettings> = themePreferences.themeSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeSettings()
        )

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.updateThemeMode(themeMode)
        }
    }

    fun updateColorScheme(colorScheme: ColorScheme) {
        viewModelScope.launch {
            themePreferences.updateColorScheme(colorScheme)
        }
    }

    fun updateDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            themePreferences.updateDynamicColor(useDynamicColor)
        }
    }

    fun updateFontScale(fontScale: FontScale) {
        viewModelScope.launch {
            themePreferences.updateFontScale(fontScale)
        }
    }

    fun updateHighContrast(highContrast: Boolean) {
        viewModelScope.launch {
            themePreferences.updateHighContrast(highContrast)
        }
    }
}
