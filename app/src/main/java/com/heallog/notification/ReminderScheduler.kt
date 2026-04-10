package com.heallog.notification

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.heallog.data.local.entity.NotificationSetting
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun scheduleReminder(setting: NotificationSetting) {
        if (!setting.isEnabled) {
            cancelReminder(setting.id)
            return
        }

        val times = parseTimes(setting.times)
        if (times.isEmpty()) return

        val now = LocalTime.now()
        val today = LocalDate.now()
        val todayDayOfWeek = today.dayOfWeek.value  // 1=Mon … 7=Sun
        val repeatDays = parseDays(setting.repeatDays)

        // Find the next scheduled fire time across the configured times
        val nextDateTime = times.sorted().let { sortedTimes ->
            // Try later today first (if today matches repeat-days)
            val todayMatches = repeatDays.isEmpty() || todayDayOfWeek in repeatDays
            val laterToday = if (todayMatches) {
                sortedTimes.firstOrNull { it.isAfter(now) }
                    ?.let { LocalDateTime.of(today, it) }
            } else null

            laterToday ?: run {
                // Find the next day that matches, starting from tomorrow
                var candidate = today.plusDays(1)
                repeat(7) {
                    val dayOfWeek = candidate.dayOfWeek.value
                    if (repeatDays.isEmpty() || dayOfWeek in repeatDays) {
                        return@run LocalDateTime.of(candidate, sortedTimes.first())
                    }
                    candidate = candidate.plusDays(1)
                }
                // Fallback: tomorrow at first time
                LocalDateTime.of(today.plusDays(1), sortedTimes.first())
            }
        }

        val delayMillis = java.time.Duration.between(LocalDateTime.now(), nextDateTime).toMillis()
        if (delayMillis <= 0) return

        val inputData = workDataOf(
            ReminderWorker.KEY_SETTING_ID to setting.id,
            ReminderWorker.KEY_TYPE to setting.type
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${setting.type}")
            .build()

        workManager.enqueueUniqueWork(
            workName(setting),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(settingId: Long) {
        workManager.cancelUniqueWork("reminder_PAIN_LOG_$settingId")
        workManager.cancelUniqueWork("reminder_HOSPITAL_$settingId")
        workManager.cancelUniqueWork("reminder_MEDICATION_$settingId")
    }

    fun cancelReminderByType(type: String, settingId: Long) {
        workManager.cancelUniqueWork("reminder_${type}_$settingId")
    }

    fun rescheduleAll(settings: List<NotificationSetting>) {
        settings.forEach { scheduleReminder(it) }
    }

    private fun workName(setting: NotificationSetting) = "reminder_${setting.type}_${setting.id}"

    private fun parseTimes(json: String): List<LocalTime> = try {
        Json.decodeFromString<List<String>>(json).mapNotNull { timeStr ->
            try { LocalTime.parse(timeStr, timeFormatter) } catch (e: Exception) { null }
        }
    } catch (e: Exception) { emptyList() }

    private fun parseDays(json: String): List<Int> = try {
        Json.decodeFromString(json)
    } catch (e: Exception) { emptyList() }
}
