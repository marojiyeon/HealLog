package com.heallog.data.repository

import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjuryRepository @Inject constructor(
    private val injuryDao: InjuryDao,
    private val painLogDao: PainLogDao
) {

    // --- Injury ---

    fun getAllActiveInjuries(): Flow<List<Injury>> = injuryDao.getAllActiveInjuries()

    fun getAllInjuries(): Flow<List<Injury>> = injuryDao.getAllInjuries()

    fun getInjuryById(id: Long): Flow<Injury?> = injuryDao.getInjuryById(id)

    suspend fun insertInjury(injury: Injury): Long = injuryDao.insertInjury(injury)

    suspend fun updateInjury(injury: Injury) = injuryDao.updateInjury(injury)

    suspend fun deleteInjury(injury: Injury) = injuryDao.deleteInjury(injury)

    // --- PainLog ---

    fun getLogsForInjury(injuryId: Long): Flow<List<PainLog>> =
        painLogDao.getLogsForInjury(injuryId)

    fun getLatestLog(injuryId: Long): Flow<PainLog?> = painLogDao.getLatestLog(injuryId)

    suspend fun insertLog(log: PainLog): Long = painLogDao.insertLog(log)

    suspend fun updateLog(log: PainLog) = painLogDao.updateLog(log)

    suspend fun deleteLog(log: PainLog) = painLogDao.deleteLog(log)
}
