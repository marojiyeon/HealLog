package com.heallog.data.repository

import app.cash.turbine.test
import com.heallog.data.local.dao.InjuryDao
import com.heallog.data.local.dao.PainLogDao
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.model.InjuryStatus
import com.heallog.model.RecoveryTrend
import com.heallog.widget.WidgetUpdateManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class InjuryRepositoryTest {

    private lateinit var injuryDao: InjuryDao
    private lateinit var painLogDao: PainLogDao
    private lateinit var widgetUpdateManager: WidgetUpdateManager
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
        widgetUpdateManager = mockk(relaxed = false)
        coEvery { widgetUpdateManager.updateAllWidgets() } returns Unit
        repository = InjuryRepository(injuryDao, painLogDao, widgetUpdateManager)
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
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
    }

    @Test
    fun `updateInjury delegates to injuryDao`() = runTest {
        coEvery { injuryDao.updateInjury(any()) } returns Unit
        val updated = testInjury.copy(status = InjuryStatus.RECOVERING)

        repository.updateInjury(updated)

        coVerify(exactly = 1) { injuryDao.updateInjury(updated) }
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
    }

    @Test
    fun `deleteInjury delegates to injuryDao`() = runTest {
        coEvery { injuryDao.deleteInjury(testInjury) } returns Unit

        repository.deleteInjury(testInjury)

        coVerify(exactly = 1) { injuryDao.deleteInjury(testInjury) }
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
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
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
    }

    @Test
    fun `updateLog delegates to painLogDao and triggers widget update`() = runTest {
        coEvery { painLogDao.updateLog(testLog) } returns Unit

        repository.updateLog(testLog)

        coVerify(exactly = 1) { painLogDao.updateLog(testLog) }
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
    }

    @Test
    fun `deleteLog delegates to painLogDao and triggers widget update`() = runTest {
        coEvery { painLogDao.deleteLog(testLog) } returns Unit

        repository.deleteLog(testLog)

        coVerify(exactly = 1) { painLogDao.deleteLog(testLog) }
        coVerify(exactly = 1) { widgetUpdateManager.updateAllWidgets() }
    }

    // --- Business Logic Tests ---

    @Test
    fun `getRecoveryStats returns correct recoveryRate when pain improves from 10 to 2`() = runTest {
        val injury = testInjury.copy(painLevel = 10, occurredAt = LocalDate.now().minusDays(10))
        val log = testLog.copy(painLevel = 2)
        every { injuryDao.getInjuryById(1L) } returns flowOf(injury)
        every { painLogDao.getPainLogsAscForInjury(1L) } returns flowOf(listOf(log))

        repository.getRecoveryStats(1L).test {
            val stats = awaitItem()
            assertEquals(80f, stats.recoveryRate)
            assertEquals(2, stats.currentPainLevel)
            assertEquals(10, stats.initialPainLevel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getRecoveryStats returns initialPainLevel as current when no logs exist`() = runTest {
        val injury = testInjury.copy(painLevel = 8)
        every { injuryDao.getInjuryById(1L) } returns flowOf(injury)
        every { painLogDao.getPainLogsAscForInjury(1L) } returns flowOf(emptyList())

        repository.getRecoveryStats(1L).test {
            val stats = awaitItem()
            assertEquals(8, stats.currentPainLevel)
            assertEquals(0f, stats.recoveryRate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getRecoveryStats returns daysSinceInjury 0 for same-day injury`() = runTest {
        val today = LocalDate.now(ZoneId.systemDefault())
        val injury = testInjury.copy(occurredAt = today)
        every { injuryDao.getInjuryById(1L) } returns flowOf(injury)
        every { painLogDao.getPainLogsAscForInjury(1L) } returns flowOf(emptyList())

        repository.getRecoveryStats(1L).test {
            val stats = awaitItem()
            assertEquals(0, stats.daysSinceInjury)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getRecoveryStats detects IMPROVING trend when recent avg is significantly lower than prev avg`() = runTest {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val injury = testInjury.copy(painLevel = 10, occurredAt = now.minusDays(15).toLocalDate())
        // prev window: [now-14d, now-7d) — avg 8.0
        val prevLog1 = testLog.copy(id = 2L, painLevel = 8, loggedAt = now.minusDays(10))
        val prevLog2 = testLog.copy(id = 3L, painLevel = 8, loggedAt = now.minusDays(9))
        // recent window: [now-7d, now] — avg 6.67
        val recentLog1 = testLog.copy(id = 4L, painLevel = 6, loggedAt = now.minusDays(6))
        val recentLog2 = testLog.copy(id = 5L, painLevel = 7, loggedAt = now.minusDays(3))
        val recentLog3 = testLog.copy(id = 6L, painLevel = 7, loggedAt = now.minusDays(1))
        val logs = listOf(prevLog1, prevLog2, recentLog1, recentLog2, recentLog3)

        every { injuryDao.getInjuryById(1L) } returns flowOf(injury)
        every { painLogDao.getPainLogsAscForInjury(1L) } returns flowOf(logs)

        repository.getRecoveryStats(1L).test {
            val stats = awaitItem()
            assertEquals(RecoveryTrend.IMPROVING, stats.trend)
            assertNotNull(stats.estimatedRecoveryDays) // 5 logs → estimation runs
            assertEquals(30f, stats.recoveryRate, 0.01f) // initial=10, current=7, (10-7)/10*100≈30
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getDashboardStats correctly filters healed injuries and computes avgRecoveryDays`() = runTest {
        val activeInjury = testInjury.copy(
            id = 1L,
            bodyPart = "left_knee",
            status = InjuryStatus.ACTIVE,
            occurredAt = LocalDate.now().minusDays(5)
        )
        val healedInjury = testInjury.copy(
            id = 2L,
            bodyPart = "right_ankle",
            status = InjuryStatus.HEALED,
            occurredAt = LocalDate.now().minusDays(40),
            updatedAt = LocalDateTime.now().minusDays(30)
        )

        every { injuryDao.getAllInjuries() } returns flowOf(listOf(activeInjury, healedInjury))
        every { injuryDao.getAllActiveInjuries() } returns flowOf(listOf(activeInjury))
        every { painLogDao.getLogsForActiveInjuries() } returns flowOf(emptyList())

        repository.getDashboardStats().test {
            val stats = awaitItem()
            assertEquals(2, stats.totalInjuries)
            assertEquals(1, stats.activeInjuries)
            assertNotNull(stats.avgRecoveryDays)
            assertEquals(1, stats.activeRecoveryList.size)
            assertEquals(activeInjury.id, stats.activeRecoveryList[0].injuryId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
