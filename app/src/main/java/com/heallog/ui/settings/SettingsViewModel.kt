package com.heallog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.preferences.VoicePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val voicePreferences: VoicePreferences
) : ViewModel() {

    val voiceFabEnabled = voicePreferences.voiceFabEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setVoiceFabEnabled(enabled: Boolean) {
        viewModelScope.launch { voicePreferences.setVoiceFabEnabled(enabled) }
    }
}
