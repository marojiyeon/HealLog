package com.heallog.data.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    fun isAvailable(): Boolean =
        HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    // Returns null when SDK is not available, preventing any lazy-init crash.
    private fun getClient(): HealthConnectClient? {
        if (!isAvailable()) return null
        return HealthConnectClient.getOrCreate(context)
    }

    suspend fun hasPermissions(): Boolean {
        val client = getClient() ?: return false
        return client.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    suspend fun getStepsToday(): Long {
        val client = getClient() ?: return 0L
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = Instant.now()
        val response = client.readRecords(
            ReadRecordsRequest(StepsRecord::class, TimeRangeFilter.between(start, end))
        )
        return response.records.sumOf { it.count }
    }

    suspend fun getSleepLastNight(): Double? {
        val client = getClient() ?: return null
        val start = LocalDate.now().minusDays(1)
            .atTime(18, 0).atZone(ZoneId.systemDefault()).toInstant()
        val end = LocalDate.now()
            .atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant()
        val response = client.readRecords(
            ReadRecordsRequest(SleepSessionRecord::class, TimeRangeFilter.between(start, end))
        )
        if (response.records.isEmpty()) return null
        val totalMs = response.records.sumOf {
            it.endTime.toEpochMilli() - it.startTime.toEpochMilli()
        }
        return totalMs / (1000.0 * 3600)
    }
}
