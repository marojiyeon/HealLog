package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.heallog.data.local.entity.PainLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PainLogDao {

    @Query("SELECT * FROM pain_logs WHERE injuryId = :injuryId ORDER BY loggedAt DESC")
    fun getLogsForInjury(injuryId: Long): Flow<List<PainLog>>

    @Query("SELECT * FROM pain_logs WHERE injuryId = :injuryId ORDER BY loggedAt DESC LIMIT 1")
    fun getLatestLog(injuryId: Long): Flow<PainLog?>

    @Query("SELECT * FROM pain_logs WHERE injuryId = :injuryId ORDER BY loggedAt ASC")
    fun getPainLogsAscForInjury(injuryId: Long): Flow<List<PainLog>>

    @Query("SELECT * FROM pain_logs ORDER BY loggedAt ASC")
    fun getAllPainLogs(): Flow<List<PainLog>>

    @Query("""
        SELECT p.* FROM pain_logs p
        INNER JOIN injuries i ON p.injuryId = i.id
        WHERE i.status != 'HEALED'
        ORDER BY p.loggedAt ASC
    """)
    fun getLogsForActiveInjuries(): Flow<List<PainLog>>

    @Insert
    suspend fun insertLog(log: PainLog): Long

    @Update
    suspend fun updateLog(log: PainLog)

    @Delete
    suspend fun deleteLog(log: PainLog)
}
