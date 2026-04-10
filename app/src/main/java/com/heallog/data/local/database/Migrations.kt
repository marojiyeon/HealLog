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
    }
}
