package com.heallog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.heallog.data.local.entity.HospitalVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface HospitalVisitDao {

    @Query("SELECT * FROM hospital_visits WHERE injuryId = :injuryId ORDER BY visitDate DESC")
    fun getVisitsForInjury(injuryId: Long): Flow<List<HospitalVisit>>

    @Query("SELECT * FROM hospital_visits WHERE nextAppointment >= date('now') ORDER BY nextAppointment ASC")
    fun getUpcomingAppointments(): Flow<List<HospitalVisit>>

    @Insert
    suspend fun insertVisit(visit: HospitalVisit): Long

    @Update
    suspend fun updateVisit(visit: HospitalVisit)

    @Delete
    suspend fun deleteVisit(visit: HospitalVisit)

    @Query("SELECT * FROM hospital_visits WHERE id = :id")
    fun getVisitById(id: Long): Flow<HospitalVisit?>

    @Query("SELECT * FROM hospital_visits ORDER BY visitDate DESC")
    fun getAllVisits(): Flow<List<HospitalVisit>>
}
