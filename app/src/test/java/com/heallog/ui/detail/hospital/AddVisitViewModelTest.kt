package com.heallog.ui.detail.hospital

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.heallog.MainDispatcherRule
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.repository.HospitalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AddVisitViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: HospitalRepository

    private val testVisit = HospitalVisit(
        id = 5L,
        injuryId = 1L,
        visitDate = LocalDate.of(2026, 4, 1),
        hospitalName = "서울병원",
        doctorName = "김의사",
        diagnosis = null,
        treatmentNote = "물리치료 권장",
        nextAppointment = null,
        cost = null,
        createdAt = LocalDateTime.now()
    )

    @Before
    fun setUp() {
        repository = mockk()
    }

    private fun createViewModel(visitId: Long = -1L): AddVisitViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("injuryId" to 1L, "visitId" to visitId)
        )
        return AddVisitViewModel(savedStateHandle, repository)
    }

    @Test
    fun `initial state has empty fields and isEditMode false`() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals("", state.hospitalName)
        assertEquals("", state.treatmentNote)
        assertFalse(state.isEditMode)
        assertFalse(state.isSaving)
    }

    @Test
    fun `saveVisit sets hospitalNameError when hospitalName is blank`() {
        val viewModel = createViewModel()
        viewModel.updateTreatmentNote("치료 내용")

        viewModel.saveVisit()

        assertTrue(viewModel.uiState.value.hospitalNameError)
    }

    @Test
    fun `saveVisit sets treatmentNoteError when treatmentNote is blank`() {
        val viewModel = createViewModel()
        viewModel.updateHospitalName("서울병원")

        viewModel.saveVisit()

        assertTrue(viewModel.uiState.value.treatmentNoteError)
    }

    @Test
    fun `saveVisit calls insertVisit and emits navigateBack on success`() = runTest {
        coEvery { repository.insertVisit(any()) } returns 1L
        val viewModel = createViewModel()
        viewModel.updateHospitalName("서울병원")
        viewModel.updateTreatmentNote("물리치료 권장")

        viewModel.navigateBack.test {
            viewModel.saveVisit()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.insertVisit(any()) }
    }

    @Test
    fun `loadExistingVisit populates all form fields in edit mode`() = runTest {
        every { repository.getVisitById(5L) } returns flowOf(testVisit)
        val viewModel = createViewModel(visitId = 5L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("서울병원", state.hospitalName)
        assertEquals("김의사", state.doctorName)
        assertEquals("물리치료 권장", state.treatmentNote)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `saveVisit in edit mode calls updateVisit not insertVisit`() = runTest {
        every { repository.getVisitById(5L) } returns flowOf(testVisit)
        coEvery { repository.updateVisit(any()) } returns Unit
        val viewModel = createViewModel(visitId = 5L)
        advanceUntilIdle()

        viewModel.navigateBack.test {
            viewModel.saveVisit()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.updateVisit(any()) }
        coVerify(exactly = 0) { repository.insertVisit(any()) }
    }
}
