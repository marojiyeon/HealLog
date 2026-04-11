package com.heallog.notification

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.heallog.data.local.entity.NotificationSetting
import com.heallog.data.local.entity.NotificationType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReminderSchedulerTest {

    private lateinit var workManager: WorkManager
    private lateinit var scheduler: ReminderScheduler

    @Before
    fun setUp() {
        workManager = mockk(relaxed = true)
        scheduler = ReminderScheduler(workManager)
    }

    // --- scheduleReminder ---

    @Test
    fun `scheduleReminder cancels work when setting is disabled`() {
        val setting = buildSetting(id = 1L, isEnabled = false, times = "[\"09:00\"]")

        scheduler.scheduleReminder(setting)

        verify { workManager.cancelUniqueWork(any()) }
    }

    @Test
    fun `scheduleReminder does nothing when times list is empty`() {
        val setting = buildSetting(id = 2L, isEnabled = true, times = "[]")

        scheduler.scheduleReminder(setting)

        verify(exactly = 0) { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun `scheduleReminder enqueues work for enabled setting with valid time`() {
        // Use a time far in the future to guarantee a positive delay
        val futureTime = LocalTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("HH:mm"))
        val setting = buildSetting(id = 3L, isEnabled = true, times = "[\"$futureTime\"]")

        scheduler.scheduleReminder(setting)

        verify {
            workManager.enqueueUniqueWork(
                "reminder_${NotificationType.PAIN_LOG}_3",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `scheduleReminder uses REPLACE policy so duplicate schedules overwrite`() {
        val futureTime = LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        val setting = buildSetting(id = 4L, isEnabled = true, times = "[\"$futureTime\"]")
        val policySlot = slot<ExistingWorkPolicy>()

        every {
            workManager.enqueueUniqueWork(any(), capture(policySlot), any<OneTimeWorkRequest>())
        } returns mockk()

        scheduler.scheduleReminder(setting)

        assertNotNull(policySlot.captured)
        assert(policySlot.captured == ExistingWorkPolicy.REPLACE)
    }

    // --- cancelReminder ---

    @Test
    fun `cancelReminder cancels all three notification type work names`() {
        scheduler.cancelReminder(settingId = 5L)

        verify { workManager.cancelUniqueWork("reminder_PAIN_LOG_5") }
        verify { workManager.cancelUniqueWork("reminder_HOSPITAL_5") }
        verify { workManager.cancelUniqueWork("reminder_MEDICATION_5") }
    }

    // --- cancelReminderByType ---

    @Test
    fun `cancelReminderByType cancels only the specified type`() {
        scheduler.cancelReminderByType(NotificationType.MEDICATION, settingId = 6L)

        verify { workManager.cancelUniqueWork("reminder_MEDICATION_6") }
        verify(exactly = 0) { workManager.cancelUniqueWork("reminder_PAIN_LOG_6") }
        verify(exactly = 0) { workManager.cancelUniqueWork("reminder_HOSPITAL_6") }
    }

    // --- rescheduleAll ---

    @Test
    fun `rescheduleAll calls scheduleReminder for each setting`() {
        val futureTime = LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        val settings = listOf(
            buildSetting(id = 7L, isEnabled = true, times = "[\"$futureTime\"]"),
            buildSetting(id = 8L, isEnabled = false, times = "[\"$futureTime\"]")
        )

        scheduler.rescheduleAll(settings)

        // Enabled setting → enqueues; disabled setting → cancels
        verify { workManager.enqueueUniqueWork("reminder_${NotificationType.PAIN_LOG}_7", any(), any<OneTimeWorkRequest>()) }
        verify { workManager.cancelUniqueWork(match { it.contains("8") }) }
    }

    // --- Edge cases ---

    @Test
    fun `scheduleReminder enqueues for next day when all today times are past`() {
        val pastTime1 = LocalTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("HH:mm"))
        val pastTime2 = LocalTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        val setting = buildSetting(
            id = 10L,
            isEnabled = true,
            times = "[\"$pastTime1\",\"$pastTime2\"]",
            repeatDays = "[]"
        )

        scheduler.scheduleReminder(setting)

        verify {
            workManager.enqueueUniqueWork(
                match { it.contains("10") },
                any(),
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `scheduleReminder with empty repeatDays schedules for any day`() {
        val futureTime = LocalTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("HH:mm"))
        val setting = buildSetting(
            id = 20L,
            isEnabled = true,
            times = "[\"$futureTime\"]",
            repeatDays = "[]"
        )

        scheduler.scheduleReminder(setting)

        verify { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
        verify(exactly = 0) { workManager.cancelUniqueWork(match { it.contains("20") }) }
    }

    // --- Helpers ---

    private fun buildSetting(
        id: Long,
        isEnabled: Boolean,
        times: String,
        repeatDays: String = "[]",
        type: String = NotificationType.PAIN_LOG
    ) = NotificationSetting(
        id = id,
        type = type,
        isEnabled = isEnabled,
        times = times,
        repeatDays = repeatDays
    )
}
