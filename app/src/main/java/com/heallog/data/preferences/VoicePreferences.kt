package com.heallog.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.voiceDataStore: DataStore<Preferences> by preferencesDataStore(name = "voice_preferences")

@Singleton
class VoicePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val VOICE_FAB_ENABLED = booleanPreferencesKey("voice_fab_enabled")
    }

    val voiceFabEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[VOICE_FAB_ENABLED] ?: false }

    suspend fun setVoiceFabEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[VOICE_FAB_ENABLED] = enabled }
    }
}
