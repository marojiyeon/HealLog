package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.heallog.data.local.entity.Injury
import kotlinx.coroutines.flow.Flow

@Dao
interface InjuryDao {

    @Query("SELECT * FROM injuries WHERE status != 'HEALED' ORDER BY createdAt DESC")
    fun getAllActiveInjuries(): Flow<List<Injury>>

    @Query("SELECT * FROM injuries ORDER BY createdAt DESC")
    fun getAllInjuries(): Flow<List<Injury>>

    @Query("SELECT * FROM injuries WHERE id = :id")
    fun getInjuryById(id: Long): Flow<Injury?>

    @Insert
    suspend fun insertInjury(injury: Injury): Long

    @Update
    suspend fun updateInjury(injury: Injury)

    @Delete
    suspend fun deleteInjury(injury: Injury)
}
