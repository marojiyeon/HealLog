package com.heallog.data.repository

import com.heallog.data.healthconnect.HealthConnectManager
import javax.inject.Inject
import javax.inject.Singleton

data class HealthData(
    val stepsToday: Long,
    val sleepHours: Double?
)

@Singleton
class HealthConnectRepository @Inject constructor(
    private val manager: HealthConnectManager
) {
    fun isAvailable(): Boolean = manager.isAvailable()
    fun getPermissions(): Set<String> = manager.permissions
    suspend fun hasPermissions(): Boolean = manager.hasPermissions()

    suspend fun loadHealthData(): HealthData = HealthData(
        stepsToday = manager.getStepsToday(),
        sleepHours = manager.getSleepLastNight()
    )
}
