package com.heallog.di

import android.content.Context
import androidx.room.Room
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.database.HealLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HealLogDatabase =
        Room.databaseBuilder(
            context,
            HealLogDatabase::class.java,
            "heallog.db"
        ).build()

    @Provides
    fun provideInjuryDao(db: HealLogDatabase): InjuryDao = db.injuryDao()

    @Provides
    fun providePainLogDao(db: HealLogDatabase): PainLogDao = db.painLogDao()
}
