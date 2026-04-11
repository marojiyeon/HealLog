package com.heallog.ui.detail.hospital

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.heallog.MainDispatcherRule
import com.heallog.data.local.entity.Medication
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

@OptIn(ExperimentalCoroutinesApi::class)
class AddMedicationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: HospitalRepository

    private val testMedication = Medication(
        id = 3L,
        injuryId = 1L,
        name = "이부프로펜",
        dosage = "400mg",
        frequency = "1일 3회",
        startDate = LocalDate.of(2026, 4, 1),
        isActive = true
    )

    @Before
    fun setUp() {
        repository = mockk()
    }

    private fun createViewModel(medicationId: Long = -1L): AddMedicationViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("injuryId" to 1L, "medicationId" to medicationId)
        )
        return AddMedicationViewModel(savedStateHandle, repository)
    }

    @Test
    fun `initial state has empty name and dosage with default frequency 1일 3회`() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.dosage)
        assertEquals("1일 3회", state.frequency)
        assertFalse(state.isEditMode)
    }

    @Test
    fun `saveMedication sets nameError when name is blank`() {
        val viewModel = createViewModel()
        viewModel.updateDosage("400mg")

        viewModel.saveMedication()

        assertTrue(viewModel.uiState.value.nameError)
    }

    @Test
    fun `saveMedication sets dosageError when dosage is blank`() {
        val viewModel = createViewModel()
        viewModel.updateName("이부프로펜")

        viewModel.saveMedication()

        assertTrue(viewModel.uiState.value.dosageError)
    }

    @Test
    fun `saveMedication calls insertMedication and emits navigateBack`() = runTest {
        coEvery { repository.insertMedication(any()) } returns 1L
        val viewModel = createViewModel()
        viewModel.updateName("이부프로펜")
        viewModel.updateDosage("400mg")

        viewModel.navigateBack.test {
            viewModel.saveMedication()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.insertMedication(any()) }
    }

    @Test
    fun `loadExistingMedication populates form fields in edit mode`() = runTest {
        every { repository.getMedicationsForInjury(1L) } returns flowOf(listOf(testMedication))
        val viewModel = createViewModel(medicationId = 3L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("이부프로펜", state.name)
        assertEquals("400mg", state.dosage)
        assertEquals("1일 3회", state.frequency)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `saveMedication in edit mode calls updateMedication not insertMedication`() = runTest {
        every { repository.getMedicationsForInjury(1L) } returns flowOf(listOf(testMedication))
        coEvery { repository.updateMedication(any()) } returns Unit
        val viewModel = createViewModel(medicationId = 3L)
        advanceUntilIdle()

        viewModel.navigateBack.test {
            viewModel.saveMedication()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.updateMedication(any()) }
        coVerify(exactly = 0) { repository.insertMedication(any()) }
    }

    @Test
    fun `toggleIsActive flips isActive state`() {
        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.isActive) // default is true
        viewModel.toggleIsActive()
        assertFalse(viewModel.uiState.value.isActive)
        viewModel.toggleIsActive()
        assertTrue(viewModel.uiState.value.isActive)
    }
}
