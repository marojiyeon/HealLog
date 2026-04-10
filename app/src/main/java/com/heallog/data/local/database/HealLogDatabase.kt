package com.heallog.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heallog.data.local.dao.HospitalVisitDao
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.MedicationDao
import com.heallog.data.local.dao.NotificationDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.Medication
import com.heallog.data.local.entity.NotificationSetting
import com.heallog.data.local.entity.PainLog

@Database(
    entities = [Injury::class, PainLog::class, HospitalVisit::class, Medication::class, NotificationSetting::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HealLogDatabase : RoomDatabase() {
    abstract fun injuryDao(): InjuryDao
    abstract fun painLogDao(): PainLogDao
    abstract fun hospitalVisitDao(): HospitalVisitDao
    abstract fun medicationDao(): MedicationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var instance: HealLogDatabase? = null

        fun getInstance(context: Context): HealLogDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HealLogDatabase::class.java,
                    "heallog.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
