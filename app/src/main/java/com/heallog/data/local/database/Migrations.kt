package com.heallog.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE injuries ADD COLUMN updatedAt TEXT")
        db.execSQL("ALTER TABLE pain_logs ADD COLUMN updatedAt TEXT")
    }
}
