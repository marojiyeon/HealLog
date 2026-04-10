package com.heallog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.dao.NotificationDao
import com.heallog.data.local.entity.NotificationSetting
import com.heallog.data.local.entity.NotificationType
import com.heallog.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class NotificationSettingsUiState(
    val painLogSetting: NotificationSetting = defaultSetting(NotificationType.PAIN_LOG),
    val hospitalSetting: NotificationSetting = defaultSetting(NotificationType.HOSPITAL),
    val medicationSetting: NotificationSetting = defaultSetting(NotificationType.MEDICATION),
    val isLoading: Boolean = true
)

private fun defaultSetting(type: String) = NotificationSetting(
    type = type,
    isEnabled = false,
    times = when (type) {
        NotificationType.PAIN_LOG -> "[\"09:00\",\"21:00\"]"
        NotificationType.MEDICATION -> "[\"08:00\",\"13:00\",\"20:00\"]"
        else -> "[]"
    },
    repeatDays = "[]"
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationDao: NotificationDao,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val uiState: StateFlow<NotificationSettingsUiState> = combine(
        notificationDao.getSettingByType(NotificationType.PAIN_LOG),
        notificationDao.getSettingByType(NotificationType.HOSPITAL),
        notificationDao.getSettingByType(NotificationType.MEDICATION)
    ) { painLog, hospital, medication ->
        NotificationSettingsUiState(
            painLogSetting = painLog ?: defaultSetting(NotificationType.PAIN_LOG),
            hospitalSetting = hospital ?: defaultSetting(NotificationType.HOSPITAL),
            medicationSetting = medication ?: defaultSetting(NotificationType.MEDICATION),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotificationSettingsUiState()
    )

    fun updatePainLogEnabled(enabled: Boolean) = viewModelScope.launch {
        val current = uiState.value.painLogSetting
        val updated = current.copy(isEnabled = enabled)
        saveAndSchedule(updated)
    }

    fun updateHospitalEnabled(enabled: Boolean) = viewModelScope.launch {
        val current = uiState.value.hospitalSetting
        val updated = current.copy(isEnabled = enabled)
        saveAndSchedule(updated)
    }

    fun updateMedicationEnabled(enabled: Boolean) = viewModelScope.launch {
        val current = uiState.value.medicationSetting
        val updated = current.copy(isEnabled = enabled)
        saveAndSchedule(updated)
    }

    fun updatePainLogTimes(times: List<String>) = viewModelScope.launch {
        val current = uiState.value.painLogSetting
        val updated = current.copy(times = Json.encodeToString(times))
        saveAndSchedule(updated)
    }

    fun updatePainLogDays(days: List<Int>) = viewModelScope.launch {
        val current = uiState.value.painLogSetting
        val updated = current.copy(repeatDays = Json.encodeToString(days))
        saveAndSchedule(updated)
    }

    fun updateMedicationTimes(times: List<String>) = viewModelScope.launch {
        val current = uiState.value.medicationSetting
        val updated = current.copy(times = Json.encodeToString(times))
        saveAndSchedule(updated)
    }

    fun updateDoNotDisturb(type: String, startTime: String?, endTime: String?) = viewModelScope.launch {
        val current = when (type) {
            NotificationType.PAIN_LOG -> uiState.value.painLogSetting
            NotificationType.HOSPITAL -> uiState.value.hospitalSetting
            NotificationType.MEDICATION -> uiState.value.medicationSetting
            else -> return@launch
        }
        val updated = current.copy(
            doNotDisturbStart = startTime,
            doNotDisturbEnd = endTime
        )
        saveAndSchedule(updated)
    }

    private suspend fun saveAndSchedule(setting: NotificationSetting) {
        val id = notificationDao.insert(setting)
        val savedSetting = if (setting.id == 0L) setting.copy(id = id) else setting
        reminderScheduler.scheduleReminder(savedSetting)
    }
}
