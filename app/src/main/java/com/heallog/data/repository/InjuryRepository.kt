package com.heallog.data.repository

import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.ChartPeriod
import com.heallog.model.DashboardStats
import com.heallog.model.PainChartPoint
import com.heallog.model.RecoveryStats
import com.heallog.model.InjuryStatus
import com.heallog.model.RecoveryTrend
import com.heallog.widget.WidgetUpdateManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing injuries and pain logs.
 *
 * This repository delegates all data access to DAOs and coordinates
 * widget updates through the WidgetUpdateManager when data changes.
 */
@Singleton
class InjuryRepository @Inject constructor(
    private val injuryDao: InjuryDao,
    private val painLogDao: PainLogDao,
    private val widgetUpdateManager: WidgetUpdateManager
) {

    private val systemZone: ZoneId = ZoneId.systemDefault()

    // --- Injury ---

    fun getAllActiveInjuries(): Flow<List<Injury>> = injuryDao.getAllActiveInjuries()

    fun getAllInjuries(): Flow<List<Injury>> = injuryDao.getAllInjuries()

    fun getInjuryById(id: Long): Flow<Injury?> = injuryDao.getInjuryById(id)

    suspend fun insertInjury(injury: Injury): Long {
        val result = injuryDao.insertInjury(injury)
        widgetUpdateManager.updateAllWidgets()
        return result
    }

    suspend fun updateInjury(injury: Injury) {
        injuryDao.updateInjury(injury)
        widgetUpdateManager.updateAllWidgets()
    }

    suspend fun deleteInjury(injury: Injury) {
        injuryDao.deleteInjury(injury)
        widgetUpdateManager.updateAllWidgets()
    }

    // --- PainLog ---

    fun getLogsForInjury(injuryId: Long): Flow<List<PainLog>> =
        painLogDao.getLogsForInjury(injuryId)

    fun getLatestLog(injuryId: Long): Flow<PainLog?> = painLogDao.getLatestLog(injuryId)

    suspend fun insertLog(log: PainLog): Long {
        val result = painLogDao.insertLog(log)
        widgetUpdateManager.updateAllWidgets()
        return result
    }

    suspend fun updateLog(log: PainLog) {
        painLogDao.updateLog(log)
        widgetUpdateManager.updateAllWidgets()
    }

    suspend fun deleteLog(log: PainLog) {
        painLogDao.deleteLog(log)
        widgetUpdateManager.updateAllWidgets()
    }

    // --- Chart & Recovery ---

    fun getPainLogsAscForInjury(injuryId: Long): Flow<List<PainLog>> =
        painLogDao.getPainLogsAscForInjury(injuryId)

    fun getPainChartData(injuryId: Long, period: ChartPeriod): Flow<List<PainChartPoint>> =
        painLogDao.getPainLogsAscForInjury(injuryId).map { logs ->
            val cutoff = periodCutoff(period)
            logs
                .filter { cutoff == null || !it.loggedAt.isBefore(cutoff) }
                .map { log ->
                    PainChartPoint(
                        date = log.loggedAt.toLocalDate(),
                        painLevel = log.painLevel,
                        note = log.note.takeIf { it.isNotBlank() }
                    )
                }
        }

    fun getRecoveryStats(injuryId: Long): Flow<RecoveryStats> =
        combine(
            injuryDao.getInjuryById(injuryId),
            painLogDao.getPainLogsAscForInjury(injuryId)
        ) { injury, logs ->
            injury?.let { computeRecoveryStats(it, logs) } ?: defaultRecoveryStats(injuryId)
        }

    fun getAllActiveRecoveryStats(): Flow<List<RecoveryStats>> =
        combine(
            injuryDao.getAllActiveInjuries(),
            painLogDao.getAllPainLogs()
        ) { injuries, allLogs ->
            val logsByInjury = allLogs.groupBy { it.injuryId }
            injuries.map { injury ->
                computeRecoveryStats(injury, logsByInjury[injury.id].orEmpty())
            }
        }

    private fun getActiveRecoveryStatsOptimized(): Flow<List<RecoveryStats>> =
        combine(
            injuryDao.getAllActiveInjuries(),
            painLogDao.getLogsForActiveInjuries()
        ) { injuries, allLogs ->
            val logsByInjury = allLogs.groupBy { it.injuryId }
            injuries.map { injury ->
                computeRecoveryStats(injury, logsByInjury[injury.id].orEmpty())
            }
        }

    fun getDashboardStats(): Flow<DashboardStats> =
        combine(
            injuryDao.getAllInjuries(),
            getActiveRecoveryStatsOptimized()
        ) { injuries, activeRecoveryList ->
            val active = injuries.filter { it.status != InjuryStatus.HEALED }
            val healed = injuries.filter { it.status == InjuryStatus.HEALED }
            val avgRecovery = if (healed.isNotEmpty()) {
                healed.map { injury ->
                    ChronoUnit.DAYS.between(
                        injury.occurredAt,
                        injury.updatedAt?.toLocalDate() ?: LocalDate.now(systemZone)
                    ).toFloat()
                }.average().toFloat()
            } else null
            val mostInjuredPart = injuries
                .groupingBy { it.bodyPart }
                .eachCount()
                .maxByOrNull { it.value }?.key
            DashboardStats(
                totalInjuries = injuries.size,
                activeInjuries = active.size,
                avgRecoveryDays = avgRecovery,
                mostInjuredPart = mostInjuredPart,
                activeRecoveryList = activeRecoveryList
            )
        }

    private fun periodCutoff(period: ChartPeriod): LocalDateTime? = when (period) {
        ChartPeriod.WEEK -> LocalDateTime.now(systemZone).minusDays(7)
        ChartPeriod.MONTH -> LocalDateTime.now(systemZone).minusDays(30)
        ChartPeriod.ALL -> null
    }

    private fun computeRecoveryStats(injury: Injury, logs: List<PainLog>): RecoveryStats {
        val initial = injury.painLevel
        val current = logs.lastOrNull()?.painLevel ?: initial
        val rate = if (initial > 0) {
            ((initial - current) / initial.toFloat() * 100f).coerceIn(0f, 100f)
        } else 0f
        val daysSince = ChronoUnit.DAYS.between(injury.occurredAt, LocalDate.now(systemZone)).toInt()
        val trend = determineTrend(logs)
        val estimated = estimateRecoveryDays(injury, logs)
        return RecoveryStats(
            injuryId = injury.id,
            injuryTitle = injury.title,
            bodyPart = injury.bodyPart,
            initialPainLevel = initial,
            currentPainLevel = current,
            recoveryRate = rate,
            daysSinceInjury = daysSince,
            estimatedRecoveryDays = estimated,
            trend = trend
        )
    }

    private fun determineTrend(logs: List<PainLog>): RecoveryTrend {
        if (logs.size < 3) return RecoveryTrend.STABLE
        val now = LocalDateTime.now(systemZone)
        val recentCutoff = now.minusDays(7)
        val prevCutoff = now.minusDays(14)
        val recent = logs.filter { it.loggedAt >= recentCutoff }.map { it.painLevel }
        val prev = logs.filter { it.loggedAt >= prevCutoff && it.loggedAt < recentCutoff }.map { it.painLevel }
        if (recent.isEmpty() || prev.isEmpty()) return RecoveryTrend.STABLE
        val recentAvg = recent.average()
        val prevAvg = prev.average()
        return when {
            recentAvg < prevAvg - 0.5 -> RecoveryTrend.IMPROVING
            recentAvg > prevAvg + 0.5 -> RecoveryTrend.WORSENING
            else -> RecoveryTrend.STABLE
        }
    }

    private fun estimateRecoveryDays(injury: Injury, logs: List<PainLog>): Int? {
        if (logs.size < 5) return null
        val initial = injury.painLevel.toFloat()
        val current = logs.last().painLevel.toFloat()
        if (current <= 0f) return 0
        val daysSince = ChronoUnit.DAYS.between(injury.occurredAt, LocalDate.now(systemZone)).toFloat()
        if (daysSince <= 0f) return null
        val dailyReduction = (initial - current) / daysSince
        if (dailyReduction <= 0f) return null
        return (current / dailyReduction).toInt()
    }

    private fun defaultRecoveryStats(injuryId: Long) = RecoveryStats(
        injuryId = injuryId,
        injuryTitle = "",
        bodyPart = "",
        initialPainLevel = 0,
        currentPainLevel = 0,
        recoveryRate = 0f,
        daysSinceInjury = 0,
        estimatedRecoveryDays = null,
        trend = RecoveryTrend.STABLE
    )
}
