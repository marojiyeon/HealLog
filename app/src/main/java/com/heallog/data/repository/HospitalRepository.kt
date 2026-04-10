package com.heallog.data.repository

import com.heallog.data.local.dao.HospitalVisitDao
import com.heallog.data.local.dao.MedicationDao
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Medication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val visitDao: HospitalVisitDao,
    private val medicationDao: MedicationDao
) {

    // --- HospitalVisit ---

    fun getVisitsForInjury(injuryId: Long) = visitDao.getVisitsForInjury(injuryId)

    fun getUpcomingAppointments() = visitDao.getUpcomingAppointments()

    suspend fun insertVisit(visit: HospitalVisit): Long = visitDao.insertVisit(visit)

    suspend fun updateVisit(visit: HospitalVisit) = visitDao.updateVisit(visit)

    suspend fun deleteVisit(visit: HospitalVisit) = visitDao.deleteVisit(visit)

    fun getVisitById(id: Long) = visitDao.getVisitById(id)

    // --- Medication ---

    fun getMedicationsForInjury(injuryId: Long) = medicationDao.getMedicationsForInjury(injuryId)

    fun getActiveMedications() = medicationDao.getActiveMedications()

    suspend fun insertMedication(med: Medication): Long = medicationDao.insertMedication(med)

    suspend fun updateMedication(med: Medication) = medicationDao.updateMedication(med)

    suspend fun deleteMedication(med: Medication) = medicationDao.deleteMedication(med)
}
