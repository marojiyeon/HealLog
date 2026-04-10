package com.heallog.data.repository

import app.cash.turbine.test
import com.heallog.data.local.dao.HospitalVisitDao
import com.heallog.data.local.dao.MedicationDao
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Medication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class HospitalRepositoryTest {

    private lateinit var visitDao: HospitalVisitDao
    private lateinit var medicationDao: MedicationDao
    private lateinit var repository: HospitalRepository

    private val testVisit = HospitalVisit(
        id = 1L,
        injuryId = 10L,
        visitDate = LocalDate.of(2026, 4, 1),
        hospitalName = "서울병원",
        doctorName = "김의사",
        treatmentNote = "물리치료 권장",
        createdAt = LocalDateTime.now()
    )

    private val testMedication = Medication(
        id = 1L,
        injuryId = 10L,
        name = "이부프로펜",
        dosage = "400mg",
        frequency = "하루 3회",
        startDate = LocalDate.of(2026, 4, 1)
    )

    @Before
    fun setUp() {
        visitDao = mockk()
        medicationDao = mockk()
        repository = HospitalRepository(visitDao, medicationDao)
    }

    // --- HospitalVisit ---

    @Test
    fun `getVisitsForInjury delegates to visitDao`() = runTest {
        every { visitDao.getVisitsForInjury(10L) } returns flowOf(listOf(testVisit))

        repository.getVisitsForInjury(10L).test {
            assertEquals(listOf(testVisit), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getUpcomingAppointments delegates to visitDao`() = runTest {
        every { visitDao.getUpcomingAppointments() } returns flowOf(listOf(testVisit))

        repository.getUpcomingAppointments().test {
            assertEquals(listOf(testVisit), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getVisitById delegates to visitDao`() = runTest {
        every { visitDao.getVisitById(1L) } returns flowOf(testVisit)

        repository.getVisitById(1L).test {
            assertEquals(testVisit, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertVisit delegates to visitDao and returns id`() = runTest {
        coEvery { visitDao.insertVisit(testVisit) } returns 1L

        val result = repository.insertVisit(testVisit)

        assertEquals(1L, result)
        coVerify(exactly = 1) { visitDao.insertVisit(testVisit) }
    }

    @Test
    fun `updateVisit delegates to visitDao`() = runTest {
        coEvery { visitDao.updateVisit(any()) } returns Unit
        val updated = testVisit.copy(diagnosis = "염좌")

        repository.updateVisit(updated)

        coVerify(exactly = 1) { visitDao.updateVisit(updated) }
    }

    @Test
    fun `deleteVisit delegates to visitDao`() = runTest {
        coEvery { visitDao.deleteVisit(testVisit) } returns Unit

        repository.deleteVisit(testVisit)

        coVerify(exactly = 1) { visitDao.deleteVisit(testVisit) }
    }

    // --- Medication ---

    @Test
    fun `getMedicationsForInjury delegates to medicationDao`() = runTest {
        every { medicationDao.getMedicationsForInjury(10L) } returns flowOf(listOf(testMedication))

        repository.getMedicationsForInjury(10L).test {
            assertEquals(listOf(testMedication), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getActiveMedications delegates to medicationDao`() = runTest {
        every { medicationDao.getActiveMedications() } returns flowOf(listOf(testMedication))

        repository.getActiveMedications().test {
            assertEquals(listOf(testMedication), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertMedication delegates to medicationDao and returns id`() = runTest {
        coEvery { medicationDao.insertMedication(testMedication) } returns 5L

        val result = repository.insertMedication(testMedication)

        assertEquals(5L, result)
        coVerify(exactly = 1) { medicationDao.insertMedication(testMedication) }
    }

    @Test
    fun `updateMedication delegates to medicationDao`() = runTest {
        coEvery { medicationDao.updateMedication(any()) } returns Unit
        val updated = testMedication.copy(isActive = false)

        repository.updateMedication(updated)

        coVerify(exactly = 1) { medicationDao.updateMedication(updated) }
    }

    @Test
    fun `deleteMedication delegates to medicationDao`() = runTest {
        coEvery { medicationDao.deleteMedication(testMedication) } returns Unit

        repository.deleteMedication(testMedication)

        coVerify(exactly = 1) { medicationDao.deleteMedication(testMedication) }
    }
}
