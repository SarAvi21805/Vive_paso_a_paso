package com.example.vivepasoapaso

import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.remote.OpenWeatherResponse
import com.example.vivepasoapaso.data.repository.HabitRepositoryContract
import com.example.vivepasoapaso.presentation.habits.HabitState
import com.example.vivepasoapaso.presentation.habits.HabitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Before
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveHabitRecord_success_updatesStateAndReloadsUserRecords() = runTest(dispatcher) {
        val repository = FakeHabitRepository().apply {
            saveResult = Result.success(testRecord)
            todayRecords = listOf(testRecord)
        }
        val viewModel = HabitViewModel(repository)

        viewModel.saveHabitRecord(testRecord)
        advanceUntilIdle()

        assertEquals(HabitState.Success, viewModel.habitState.value)
        assertSame(testRecord, repository.savedRecord)
        assertEquals(listOf("user-1"), repository.todayRecordsRequests)
        assertEquals(listOf(testRecord), viewModel.todayRecords.value)
    }

    @Test
    fun saveHabitRecord_failure_updatesErrorAndStillReloadsUserRecords() = runTest(dispatcher) {
        val repository = FakeHabitRepository().apply {
            saveResult = Result.failure(IllegalStateException("fallo de prueba"))
        }
        val viewModel = HabitViewModel(repository)

        viewModel.saveHabitRecord(testRecord)
        advanceUntilIdle()

        assertEquals(HabitState.Error("fallo de prueba"), viewModel.habitState.value)
        assertSame(testRecord, repository.savedRecord)
        assertEquals(listOf("user-1"), repository.todayRecordsRequests)
    }

    @Test
    fun loadTodayRecords_success_updatesRecordsAndUsesUserId() = runTest(dispatcher) {
        val repository = FakeHabitRepository().apply {
            todayRecords = listOf(testRecord)
        }
        val viewModel = HabitViewModel(repository)

        viewModel.loadTodayRecords("user-1")
        advanceUntilIdle()

        assertEquals(listOf(testRecord), viewModel.todayRecords.value)
        assertEquals(listOf("user-1"), repository.todayRecordsRequests)
    }

    @Test
    fun loadTodayRecords_emptyResult_updatesEmptyListWithoutError() = runTest(dispatcher) {
        val repository = FakeHabitRepository().apply {
            todayRecords = emptyList()
        }
        val viewModel = HabitViewModel(repository)

        viewModel.loadTodayRecords("user-1")
        advanceUntilIdle()

        assertEquals(emptyList<HabitRecord>(), viewModel.todayRecords.value)
        assertEquals(listOf("user-1"), repository.todayRecordsRequests)
        assertEquals(HabitState.Idle, viewModel.habitState.value)
    }

    @Test
    fun loadTodayRecords_failure_updatesErrorStateWithoutEscapingException() = runTest(dispatcher) {
        val repository = FakeHabitRepository().apply {
            todayRecordsError = IllegalStateException("fallo de lectura")
        }
        val viewModel = HabitViewModel(repository)

        viewModel.loadTodayRecords("user-1")
        advanceUntilIdle()

        assertEquals(HabitState.Error("fallo de lectura"), viewModel.habitState.value)
        assertEquals(listOf("user-1"), repository.todayRecordsRequests)
    }

    private class FakeHabitRepository : HabitRepositoryContract {
        var saveResult: Result<HabitRecord> = Result.success(testRecord)
        var savedRecord: HabitRecord? = null
        var todayRecords: List<HabitRecord> = emptyList()
        var todayRecordsError: Throwable? = null
        val todayRecordsRequests = mutableListOf<String>()

        override suspend fun saveHabitRecord(record: HabitRecord): Result<HabitRecord> {
            savedRecord = record
            return saveResult
        }

        override suspend fun getTodayHabitRecords(userId: String): List<HabitRecord> {
            todayRecordsRequests += userId
            todayRecordsError?.let { throw it }
            return todayRecords
        }

        override suspend fun getWeeklyStats(userId: String): Map<HabitType, Double> = emptyMap()

        override suspend fun getHabitRecordsByType(
            userId: String,
            type: HabitType,
            limit: Int
        ): List<HabitRecord> = emptyList()

        override suspend fun getCaloriesForFood(foodQuery: String): Double? = null

        override suspend fun getCurrentWeather(lat: Double, lon: Double): OpenWeatherResponse? = null
    }

    companion object {
        private val testRecord = HabitRecord(
            id = "habit-1",
            userId = "user-1",
            type = HabitType.EXERCISE,
            value = 30.0
        )
    }
}
