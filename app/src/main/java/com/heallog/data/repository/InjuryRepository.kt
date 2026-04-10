package com.heallog.data.repository

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.widget.HealLogSmallWidget
import com.heallog.widget.HealLogMediumWidget
import com.heallog.widget.HealLogLargeWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjuryRepository @Inject constructor(
    private val injuryDao: InjuryDao,
    private val painLogDao: PainLogDao,
    @ApplicationContext private val context: Context
) {

    // --- Injury ---

    fun getAllActiveInjuries(): Flow<List<Injury>> = injuryDao.getAllActiveInjuries()

    fun getAllInjuries(): Flow<List<Injury>> = injuryDao.getAllInjuries()

    fun getInjuryById(id: Long): Flow<Injury?> = injuryDao.getInjuryById(id)

    suspend fun insertInjury(injury: Injury): Long {
        val result = injuryDao.insertInjury(injury)
        updateWidgets()
        return result
    }

    suspend fun updateInjury(injury: Injury) {
        injuryDao.updateInjury(injury)
        updateWidgets()
    }

    suspend fun deleteInjury(injury: Injury) {
        injuryDao.deleteInjury(injury)
        updateWidgets()
    }

    // --- PainLog ---

    fun getLogsForInjury(injuryId: Long): Flow<List<PainLog>> =
        painLogDao.getLogsForInjury(injuryId)

    fun getLatestLog(injuryId: Long): Flow<PainLog?> = painLogDao.getLatestLog(injuryId)

    suspend fun insertLog(log: PainLog): Long {
        val result = painLogDao.insertLog(log)
        updateWidgets()
        return result
    }

    suspend fun updateLog(log: PainLog) {
        painLogDao.updateLog(log)
        updateWidgets()
    }

    suspend fun deleteLog(log: PainLog) {
        painLogDao.deleteLog(log)
        updateWidgets()
    }

    private suspend fun updateWidgets() {
        try {
            val glanceManager = GlanceAppWidgetManager(context)
            HealLogSmallWidget().updateAll(glanceManager, context)
            HealLogMediumWidget().updateAll(glanceManager, context)
            HealLogLargeWidget().updateAll(glanceManager, context)
        } catch (e: Exception) {
            // Silently fail if widgets are not installed
        }
    }
}
