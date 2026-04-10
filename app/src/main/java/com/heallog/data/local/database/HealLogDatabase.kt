package com.heallog.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heallog.data.local.dao.HospitalVisitDao
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.MedicationDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.Medication
import com.heallog.data.local.entity.PainLog

@Database(
    entities = [Injury::class, PainLog::class, HospitalVisit::class, Medication::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HealLogDatabase : RoomDatabase() {
    abstract fun injuryDao(): InjuryDao
    abstract fun painLogDao(): PainLogDao
    abstract fun hospitalVisitDao(): HospitalVisitDao
    abstract fun medicationDao(): MedicationDao
}
