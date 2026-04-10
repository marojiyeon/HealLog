package com.heallog.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.heallog.MainActivity
import com.heallog.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_PAIN_REMINDER = "pain_reminder"
        const val CHANNEL_HOSPITAL_REMINDER = "hospital_reminder"
        const val CHANNEL_MEDICATION_REMINDER = "medication_reminder"

        const val NOTIFICATION_ID_PAIN = 1001
        const val NOTIFICATION_ID_HOSPITAL = 1002
        const val NOTIFICATION_ID_MEDICATION = 1003

        const val EXTRA_SNOOZE_NOTIFICATION_ID = "snooze_notification_id"
        const val EXTRA_SNOOZE_SETTING_ID = "snooze_setting_id"

        fun createChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(NotificationManager::class.java)
                listOf(
                    NotificationChannel(
                        CHANNEL_PAIN_REMINDER,
                        "통증 기록 알림",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "통증 기록 시간을 알려드려요" },
                    NotificationChannel(
                        CHANNEL_HOSPITAL_REMINDER,
                        "병원 예약 알림",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply { description = "병원 예약을 알려드려요" },
                    NotificationChannel(
                        CHANNEL_MEDICATION_REMINDER,
                        "약 복용 알림",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "약 복용 시간을 알려드려요" }
                ).forEach { manager.createNotificationChannel(it) }
            }
        }
    }

    fun showPainReminderNotification(injuryTitle: String? = null, settingId: Long = -1L) {
        val body = if (injuryTitle != null) "$injuryTitle 의 통증을 기록해주세요" else "오늘의 통증을 기록해주세요"
        val notification = NotificationCompat.Builder(context, CHANNEL_PAIN_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("통증 기록 시간이에요")
            .setContentText(body)
            .setContentIntent(mainPendingIntent())
            .addAction(0, "지금 기록하기", mainPendingIntent())
            .addAction(0, "나중에", snoozePendingIntent(NOTIFICATION_ID_PAIN, settingId))
            .setAutoCancel(true)
            .build()
        notify(NOTIFICATION_ID_PAIN, notification)
    }

    fun showHospitalReminderNotification(detail: String? = null, settingId: Long = -1L) {
        val body = detail ?: "병원 예약을 확인해주세요"
        val notification = NotificationCompat.Builder(context, CHANNEL_HOSPITAL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("병원 예약 알림")
            .setContentText(body)
            .setContentIntent(mainPendingIntent())
            .addAction(0, "지금 기록하기", mainPendingIntent())
            .addAction(0, "나중에", snoozePendingIntent(NOTIFICATION_ID_HOSPITAL, settingId))
            .setAutoCancel(true)
            .build()
        notify(NOTIFICATION_ID_HOSPITAL, notification)
    }

    fun showMedicationReminderNotification(detail: String? = null, settingId: Long = -1L) {
        val body = detail ?: "약 복용 시간이에요"
        val notification = NotificationCompat.Builder(context, CHANNEL_MEDICATION_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("약 복용 시간")
            .setContentText(body)
            .setContentIntent(mainPendingIntent())
            .addAction(0, "지금 기록하기", mainPendingIntent())
            .addAction(0, "나중에", snoozePendingIntent(NOTIFICATION_ID_MEDICATION, settingId))
            .setAutoCancel(true)
            .build()
        notify(NOTIFICATION_ID_MEDICATION, notification)
    }

    private fun mainPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun snoozePendingIntent(notificationId: Int, settingId: Long): PendingIntent {
        val intent = Intent(context, SnoozeReceiver::class.java).apply {
            putExtra(EXTRA_SNOOZE_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_SNOOZE_SETTING_ID, settingId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun notify(id: Int, notification: android.app.Notification) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            manager.areNotificationsEnabled()
        ) {
            manager.notify(id, notification)
        }
    }
}
