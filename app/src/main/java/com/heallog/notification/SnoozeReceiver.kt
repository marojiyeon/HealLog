package com.heallog.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.heallog.data.local.dao.NotificationDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

@AndroidEntryPoint
class SnoozeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(
            NotificationHelper.EXTRA_SNOOZE_NOTIFICATION_ID, -1
        )
        val settingId = intent.getLongExtra(
            NotificationHelper.EXTRA_SNOOZE_SETTING_ID, -1L
        )

        // Dismiss the notification
        if (notificationId != -1) {
            context.getSystemService(NotificationManager::class.java)
                .cancel(notificationId)
        }

        if (settingId == -1L) return

        // Schedule a one-time reminder 1 hour later
        CoroutineScope(Dispatchers.IO).launch {
            val setting = notificationDao.getSettingById(settingId) ?: return@launch
            val inputData = workDataOf(
                ReminderWorker.KEY_SETTING_ID to setting.id,
                ReminderWorker.KEY_TYPE to setting.type
            )
            val snoozeRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(1, TimeUnit.HOURS)
                .setInputData(inputData)
                .build()
            workManager.enqueue(snoozeRequest)
        }
    }
}
