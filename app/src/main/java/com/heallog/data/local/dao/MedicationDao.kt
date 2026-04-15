package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.heallog.data.local.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE injuryId = :injuryId ORDER BY startDate DESC")
    fun getMedicationsForInjury(injuryId: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications ORDER BY startDate DESC")
    fun getAllMedications(): Flow<List<Medication>>

    @Insert
    suspend fun insertMedication(med: Medication): Long

    @Update
    suspend fun updateMedication(med: Medication)

    @Delete
    suspend fun deleteMedication(med: Medication)
}
