package com.heallog.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.heallog.data.datastore.ThemePreferences
import com.heallog.data.datastore.UserProfileStore
import com.heallog.data.preferences.voiceDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }

    @Singleton
    @Provides
    fun provideUserProfileStore(@ApplicationContext context: Context): UserProfileStore {
        return UserProfileStore(context)
    }

    @Provides
    @Singleton
    fun provideVoiceDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.voiceDataStore
}
