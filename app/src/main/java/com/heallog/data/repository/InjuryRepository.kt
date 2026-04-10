package com.heallog.data.repository

import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.widget.WidgetUpdateManager
import kotlinx.coroutines.flow.Flow
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
}
