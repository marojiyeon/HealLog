package com.heallog.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE injuries ADD COLUMN updatedAt TEXT")
        db.execSQL("ALTER TABLE pain_logs ADD COLUMN updatedAt TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `hospital_visits` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `injuryId` INTEGER NOT NULL,
                `visitDate` TEXT NOT NULL,
                `hospitalName` TEXT NOT NULL,
                `doctorName` TEXT,
                `diagnosis` TEXT,
                `treatmentNote` TEXT NOT NULL,
                `nextAppointment` TEXT,
                `cost` INTEGER,
                `isInsuranceCovered` INTEGER,
                `photoUris` TEXT,
                `createdAt` TEXT NOT NULL,
                `updatedAt` TEXT,
                FOREIGN KEY(`injuryId`) REFERENCES `injuries`(`id`) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `medications` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `injuryId` INTEGER NOT NULL,
                `hospitalVisitId` INTEGER,
                `name` TEXT NOT NULL,
                `dosage` TEXT NOT NULL,
                `frequency` TEXT NOT NULL,
                `startDate` TEXT NOT NULL,
                `endDate` TEXT,
                `sideEffectNote` TEXT,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                `updatedAt` TEXT,
                FOREIGN KEY(`injuryId`) REFERENCES `injuries`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`hospitalVisitId`) REFERENCES `hospital_visits`(`id`) ON DELETE SET NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_hospital_visits_injuryId` ON `hospital_visits` (`injuryId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medications_injuryId` ON `medications` (`injuryId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medications_hospitalVisitId` ON `medications` (`hospitalVisitId`)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS notification_settings (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                isEnabled INTEGER NOT NULL DEFAULT 1,
                times TEXT NOT NULL DEFAULT '[]',
                repeatDays TEXT NOT NULL DEFAULT '[]',
                intervalHours INTEGER,
                injuryId INTEGER,
                doNotDisturbStart TEXT,
                doNotDisturbEnd TEXT
            )
            """.trimIndent()
        )
        // Add missing indices from MIGRATION_2_3 (IF NOT EXISTS is safe if already present)
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_hospital_visits_injuryId` ON `hospital_visits` (`injuryId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medications_injuryId` ON `medications` (`injuryId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medications_hospitalVisitId` ON `medications` (`hospitalVisitId`)")
    }
}
