package com.heallog.ui.record

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.heallog.MainDispatcherRule
import com.heallog.data.local.entity.Injury
import com.heallog.data.repository.InjuryRepository
import com.heallog.model.InjuryStatus
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class RecordInjuryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: InjuryRepository
    private lateinit var viewModel: RecordInjuryViewModel

    @Before
    fun setUp() {
        repository = mockk()
        val savedStateHandle = SavedStateHandle(mapOf("bodyPartId" to "left_knee"))
        viewModel = RecordInjuryViewModel(savedStateHandle, repository)
    }

    @Test
    fun `initial state has correct bodyPartId and Korean name`() {
        val state = viewModel.uiState.value
        assertEquals("left_knee", state.bodyPartId)
        assertEquals("왼쪽 무릎", state.bodyPartNameKo)
    }

    @Test
    fun `initial state has empty title and no errors`() {
        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertFalse(state.titleError)
        assertFalse(state.painLevelError)
        assertFalse(state.isSaving)
    }

    @Test
    fun `updateTitle clears titleError`() {
        // First trigger an error
        viewModel.saveInjury()
        assertTrue(viewModel.uiState.value.titleError)

        // Then fix it
        viewModel.updateTitle("새 부상")
        assertEquals("새 부상", viewModel.uiState.value.title)
        assertFalse(viewModel.uiState.value.titleError)
    }

    @Test
    fun `updatePainLevel sets touched flag and clears error`() {
        viewModel.updatePainLevel(7)
        val state = viewModel.uiState.value
        assertEquals(7, state.painLevel)
        assertTrue(state.isPainLevelTouched)
        assertFalse(state.painLevelError)
    }

    @Test
    fun `updateDescription updates state`() {
        viewModel.updateDescription("계단에서 넘어짐")
        assertEquals("계단에서 넘어짐", viewModel.uiState.value.description)
    }

    @Test
    fun `saveInjury sets titleError when title is blank`() {
        viewModel.updatePainLevel(5)
        viewModel.saveInjury()
        assertTrue(viewModel.uiState.value.titleError)
    }

    @Test
    fun `saveInjury sets painLevelError when pain not touched`() {
        viewModel.updateTitle("부상 제목")
        viewModel.saveInjury()
        assertTrue(viewModel.uiState.value.painLevelError)
    }

    @Test
    fun `saveInjury sets both errors when title blank and pain not touched`() {
        viewModel.saveInjury()
        val state = viewModel.uiState.value
        assertTrue(state.titleError)
        assertTrue(state.painLevelError)
    }

    @Test
    fun `saveInjury calls insertInjury and emits navigateBack on success`() = runTest {
        coEvery { repository.insertInjury(any()) } returns 1L
        viewModel.updateTitle("왼쪽 무릎 부상")
        viewModel.updatePainLevel(6)

        viewModel.navigateBack.test {
            viewModel.saveInjury()
            awaitItem() // navigation event emitted
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.insertInjury(any<Injury>()) }
    }

    @Test
    fun `saveInjury trims title whitespace before saving`() = runTest {
        coEvery { repository.insertInjury(any()) } returns 1L
        viewModel.updateTitle("  왼쪽 무릎  ")
        viewModel.updatePainLevel(5)
        viewModel.saveInjury()

        coVerify {
            repository.insertInjury(match { it.title == "왼쪽 무릎" })
        }
    }

    @Test
    fun `saveInjury does not proceed when validation fails`() = runTest {
        // No title, no pain level
        viewModel.saveInjury()
        coVerify(exactly = 0) { repository.insertInjury(any()) }
    }

    @Test
    fun `saveInjury in edit mode calls updateInjury not insertInjury`() = runTest {
        val testInjury = Injury(
            id = 42L,
            bodyPart = "left_knee",
            title = "무릎 부상",
            description = "",
            painLevel = 6,
            occurredAt = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            status = InjuryStatus.ACTIVE
        )
        val editSavedStateHandle = SavedStateHandle(
            mapOf("bodyPartId" to "left_knee", "injuryId" to 42L)
        )
        every { repository.getInjuryById(42L) } returns flowOf(testInjury)
        coEvery { repository.updateInjury(any()) } returns Unit

        val editViewModel = RecordInjuryViewModel(editSavedStateHandle, repository)
        advanceUntilIdle()

        editViewModel.updatePainLevel(8)
        editViewModel.saveInjury()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.updateInjury(any()) }
        coVerify(exactly = 0) { repository.insertInjury(any()) }
    }

    @Test
    fun `addPhoto rejects fourth photo and sets photoError`() {
        val uris = (1..4).map { mockk<Uri>() }
        uris.forEach { viewModel.addPhoto(it) }

        assertEquals(3, viewModel.uiState.value.photoUris.size)
        assertNotNull(viewModel.uiState.value.photoError)
    }

    @Test
    fun `hasUnsavedChanges returns true after date change in edit mode`() = runTest {
        val testInjury = Injury(
            id = 42L,
            bodyPart = "left_knee",
            title = "무릎 부상",
            description = "",
            painLevel = 6,
            occurredAt = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            status = InjuryStatus.ACTIVE
        )
        val editSavedStateHandle = SavedStateHandle(
            mapOf("bodyPartId" to "left_knee", "injuryId" to 42L)
        )
        every { repository.getInjuryById(42L) } returns flowOf(testInjury)

        val editViewModel = RecordInjuryViewModel(editSavedStateHandle, repository)
        advanceUntilIdle()

        val originalDate = editViewModel.uiState.value.occurredDate
        editViewModel.updateDate(originalDate.plusDays(1))

        assertTrue(editViewModel.hasUnsavedChanges())
    }
}
