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
    }
}
