package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.heallog.data.local.entity.PainLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PainLogDao {

    @Query("SELECT * FROM pain_logs WHERE injuryId = :injuryId ORDER BY loggedAt DESC")
    fun getLogsForInjury(injuryId: Long): Flow<List<PainLog>>

    @Query("SELECT * FROM pain_logs WHERE injuryId = :injuryId ORDER BY loggedAt DESC LIMIT 1")
    fun getLatestLog(injuryId: Long): Flow<PainLog?>

    @Insert
    suspend fun insertLog(log: PainLog): Long

    @Delete
    suspend fun deleteLog(log: PainLog)
}
