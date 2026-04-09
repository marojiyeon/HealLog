package com.heallog.data.repository

import app.cash.turbine.test
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.InjuryStatus
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

class InjuryRepositoryTest {

    private lateinit var injuryDao: InjuryDao
    private lateinit var painLogDao: PainLogDao
    private lateinit var repository: InjuryRepository

    private val testInjury = Injury(
        id = 1L,
        bodyPart = "left_knee",
        title = "무릎 부상",
        description = "",
        painLevel = 6,
        occurredAt = LocalDate.now(),
        createdAt = LocalDateTime.now(),
        status = InjuryStatus.ACTIVE
    )

    private val testLog = PainLog(
        id = 1L,
        injuryId = 1L,
        painLevel = 5,
        note = "조금 나아짐",
        loggedAt = LocalDateTime.now()
    )

    @Before
    fun setUp() {
        injuryDao = mockk()
        painLogDao = mockk()
        repository = InjuryRepository(injuryDao, painLogDao)
    }

    @Test
    fun `getAllActiveInjuries delegates to injuryDao`() = runTest {
        every { injuryDao.getAllActiveInjuries() } returns flowOf(listOf(testInjury))

        repository.getAllActiveInjuries().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(testInjury, items[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllInjuries delegates to injuryDao`() = runTest {
        every { injuryDao.getAllInjuries() } returns flowOf(listOf(testInjury))

        repository.getAllInjuries().test {
            assertEquals(listOf(testInjury), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getInjuryById delegates to injuryDao`() = runTest {
        every { injuryDao.getInjuryById(1L) } returns flowOf(testInjury)

        repository.getInjuryById(1L).test {
            assertEquals(testInjury, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertInjury delegates to injuryDao and returns generated id`() = runTest {
        coEvery { injuryDao.insertInjury(testInjury) } returns 42L

        val result = repository.insertInjury(testInjury)

        assertEquals(42L, result)
        coVerify(exactly = 1) { injuryDao.insertInjury(testInjury) }
    }

    @Test
    fun `updateInjury delegates to injuryDao`() = runTest {
        coEvery { injuryDao.updateInjury(any()) } returns Unit
        val updated = testInjury.copy(status = InjuryStatus.RECOVERING)

        repository.updateInjury(updated)

        coVerify(exactly = 1) { injuryDao.updateInjury(updated) }
    }

    @Test
    fun `deleteInjury delegates to injuryDao`() = runTest {
        coEvery { injuryDao.deleteInjury(testInjury) } returns Unit

        repository.deleteInjury(testInjury)

        coVerify(exactly = 1) { injuryDao.deleteInjury(testInjury) }
    }

    @Test
    fun `getLogsForInjury delegates to painLogDao`() = runTest {
        every { painLogDao.getLogsForInjury(1L) } returns flowOf(listOf(testLog))

        repository.getLogsForInjury(1L).test {
            assertEquals(listOf(testLog), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getLatestLog delegates to painLogDao`() = runTest {
        every { painLogDao.getLatestLog(1L) } returns flowOf(testLog)

        repository.getLatestLog(1L).test {
            assertEquals(testLog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertLog delegates to painLogDao`() = runTest {
        coEvery { painLogDao.insertLog(testLog) } returns 10L

        val result = repository.insertLog(testLog)

        assertEquals(10L, result)
        coVerify(exactly = 1) { painLogDao.insertLog(testLog) }
    }
}
