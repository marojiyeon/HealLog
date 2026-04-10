package com.heallog.di

import android.content.Context
import androidx.room.Room
import com.heallog.data.local.dao.HospitalVisitDao
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.MedicationDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.database.HealLogDatabase
import com.heallog.data.local.database.MIGRATION_1_2
import com.heallog.data.local.database.MIGRATION_2_3
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
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideInjuryDao(db: HealLogDatabase): InjuryDao = db.injuryDao()

    @Provides
    fun providePainLogDao(db: HealLogDatabase): PainLogDao = db.painLogDao()

    @Provides
    fun provideHospitalVisitDao(db: HealLogDatabase): HospitalVisitDao = db.hospitalVisitDao()

    @Provides
    fun provideMedicationDao(db: HealLogDatabase): MedicationDao = db.medicationDao()
}
