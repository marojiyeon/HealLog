package com.heallog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_settings")
data class NotificationSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val isEnabled: Boolean = true,
    val times: String = "[]",        // JSON array of time strings: ["09:00","21:00"]
    val repeatDays: String = "[]",   // JSON array of day ints: [1,2,3,4,5] Mon-Fri, empty = every day
    val intervalHours: Int? = null,
    val injuryId: Long? = null,      // null = all active injuries
    val doNotDisturbStart: String? = null,  // e.g. "23:00"
    val doNotDisturbEnd: String? = null     // e.g. "07:00"
)

object NotificationType {
    const val PAIN_LOG = "PAIN_LOG"
    const val HOSPITAL = "HOSPITAL"
    const val MEDICATION = "MEDICATION"
}
