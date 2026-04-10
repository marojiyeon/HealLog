package com.heallog.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.heallog.data.local.dao.NotificationDao
import com.heallog.data.local.entity.NotificationSetting
import com.heallog.data.local.entity.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationDao: NotificationDao,
    private val notificationHelper: NotificationHelper,
    private val reminderScheduler: ReminderScheduler
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_SETTING_ID = "setting_id"
        const val KEY_TYPE = "type"
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override suspend fun doWork(): Result {
        val settingId = inputData.getLong(KEY_SETTING_ID, -1L)
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()

        val setting = notificationDao.getSettingById(settingId) ?: return Result.failure()

        if (!setting.isEnabled) return Result.success()

        if (!isInDoNotDisturb(setting)) {
            when (type) {
                NotificationType.PAIN_LOG ->
                    notificationHelper.showPainReminderNotification(settingId = setting.id)
                NotificationType.HOSPITAL ->
                    notificationHelper.showHospitalReminderNotification(settingId = setting.id)
                NotificationType.MEDICATION ->
                    notificationHelper.showMedicationReminderNotification(settingId = setting.id)
            }
        }

        // Always schedule the next occurrence
        reminderScheduler.scheduleReminder(setting)

        return Result.success()
    }

    private fun isInDoNotDisturb(setting: NotificationSetting): Boolean {
        val dndStart = setting.doNotDisturbStart ?: return false
        val dndEnd = setting.doNotDisturbEnd ?: return false

        return try {
            val start = LocalTime.parse(dndStart, timeFormatter)
            val end = LocalTime.parse(dndEnd, timeFormatter)
            val now = LocalTime.now()

            if (start.isBefore(end)) {
                // e.g. 08:00–12:00: now must be between start and end
                now >= start && now < end
            } else {
                // Overnight e.g. 23:00–07:00: now is after start OR before end
                now >= start || now < end
            }
        } catch (e: Exception) {
            false
        }
    }
}
