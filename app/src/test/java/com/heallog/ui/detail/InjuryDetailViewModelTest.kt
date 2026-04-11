package com.heallog.ui.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.heallog.MainDispatcherRule
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class InjuryDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: InjuryRepository
    private lateinit var viewModel: InjuryDetailViewModel

    private val testInjury = Injury(
        id = 1L,
        bodyPart = "left_knee",
        title = "왼쪽 무릎 부상",
        description = "계단에서 넘어짐",
        painLevel = 7,
        occurredAt = LocalDate.now().minusDays(10),
        createdAt = LocalDateTime.now().minusDays(10),
        status = InjuryStatus.ACTIVE
    )

    private val testLog = PainLog(
        id = 1L,
        injuryId = 1L,
        painLevel = 5,
        note = "조금 나아짐",
        loggedAt = LocalDateTime.now().minusDays(1)
    )

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.getInjuryById(1L) } returns flowOf(testInjury)
        every { repository.getLogsForInjury(1L) } returns flowOf(listOf(testLog))
        val savedStateHandle = SavedStateHandle(mapOf("injuryId" to 1L))
        viewModel = InjuryDetailViewModel(savedStateHandle, repository)
    }

    @Test
    fun `initial uiState loads injury and logs`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testInjury, state.injury)
            assertEquals(1, state.painLogs.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleLogForm expands and collapses form`() {
        assertFalse(viewModel.uiState.value.newLogForm.isExpanded)

        viewModel.toggleLogForm()
        assertTrue(viewModel.uiState.value.newLogForm.isExpanded)

        viewModel.toggleLogForm()
        assertFalse(viewModel.uiState.value.newLogForm.isExpanded)
    }

    @Test
    fun `updateLogPainLevel updates form state`() {
        viewModel.updateLogPainLevel(8)
        assertEquals(8, viewModel.uiState.value.newLogForm.painLevel)
    }

    @Test
    fun `updateLogNote updates form state`() {
        viewModel.updateLogNote("오늘도 많이 아팠다")
        assertEquals("오늘도 많이 아팠다", viewModel.uiState.value.newLogForm.note)
    }

    @Test
    fun `addPainLog inserts log and emits logSaved event`() = runTest {
        coEvery { repository.insertLog(any()) } returns 10L
        viewModel.updateLogPainLevel(6)
        viewModel.updateLogNote("기록 추가")

        viewModel.logSaved.test {
            viewModel.addPainLog()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.insertLog(any<PainLog>()) }
    }

    @Test
    fun `addPainLog resets form after save`() = runTest {
        coEvery { repository.insertLog(any()) } returns 10L
        viewModel.toggleLogForm()
        viewModel.updateLogPainLevel(8)
        viewModel.updateLogNote("기록")

        viewModel.logSaved.test {
            viewModel.addPainLog()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val form = viewModel.uiState.value.newLogForm
        assertEquals(5, form.painLevel) // default
        assertEquals("", form.note)
        assertFalse(form.isExpanded)
    }

    @Test
    fun `updateInjuryStatus calls repository updateInjury with new status`() = runTest {
        coEvery { repository.updateInjury(any()) } returns Unit

        viewModel.updateInjuryStatus(InjuryStatus.RECOVERING)

        coVerify(exactly = 1) {
            repository.updateInjury(match { it.status == InjuryStatus.RECOVERING })
        }
    }

    @Test
    fun `deleteInjury calls repository and emits navigateBack`() = runTest {
        coEvery { repository.deleteInjury(any()) } returns Unit

        viewModel.navigateBack.test {
            viewModel.deleteInjury()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { repository.deleteInjury(testInjury) }
    }

    @Test
    fun `updatePainLog calls repository updateLog with updated fields`() = runTest {
        coEvery { repository.updateLog(any()) } returns Unit

        viewModel.updatePainLog(testLog, painLevel = 8, note = "많이 나아짐", photoUris = emptyList())
        advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.updateLog(match { it.painLevel == 8 && it.note == "많이 나아짐" })
        }
    }

    @Test
    fun `deletePainLog calls repository deleteLog`() = runTest {
        coEvery { repository.deleteLog(testLog) } returns Unit

        viewModel.deletePainLog(testLog)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteLog(testLog) }
    }

    @Test
    fun `addLogPhoto ignores fourth photo`() {
        val uris = (1..4).map { mockk<Uri>() }
        uris.forEach { viewModel.addLogPhoto(it) }

        val formState = viewModel.uiState.value.newLogForm
        assertEquals(3, formState.photoUris.size)
    }

    @Test
    fun `pain logs are sorted newest first`() = runTest {
        val older = PainLog(id = 2L, injuryId = 1L, painLevel = 7, note = "", loggedAt = LocalDateTime.now().minusDays(5))
        val newer = PainLog(id = 3L, injuryId = 1L, painLevel = 4, note = "", loggedAt = LocalDateTime.now())
        every { repository.getLogsForInjury(1L) } returns flowOf(listOf(older, newer))

        val savedStateHandle = SavedStateHandle(mapOf("injuryId" to 1L))
        val vm = InjuryDetailViewModel(savedStateHandle, repository)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(newer.id, state.painLogs[0].id)
            assertEquals(older.id, state.painLogs[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
