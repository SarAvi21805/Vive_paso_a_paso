package com.example.vivepasoapaso.data.repository

import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.Calendar
import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun saveHabitRecord(record: HabitRecord): Result<HabitRecord> {
        return try {
            val recordWithId = if (record.id.isEmpty()) {
                record.copy(id = db.collection("habits").document().id)
            } else {
                record
            }

            db.collection("habits").document(recordWithId.id).set(recordWithId).await()
            Result.success(recordWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHabitRecords(userId: String, startDate: Date, endDate: Date): List<HabitRecord> {
        return try {
            val snapshot = db.collection("habits")
                .whereEqualTo("user_id", userId)
                .whereGreaterThanOrEqualTo("record_date", Timestamp(startDate))
                .whereLessThanOrEqualTo("record_date", Timestamp(endDate))
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(HabitRecord::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getHabitRecordsByType(userId: String, type: HabitType, limit: Int = 30): List<HabitRecord> {
        return try {
            val snapshot = db.collection("habits")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("type", type)
                .orderBy("record_date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(HabitRecord::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteHabitRecord(recordId: String): Result<Unit> {
        return try {
            db.collection("habits").document(recordId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodayHabitRecords(userId: String): List<HabitRecord> {
        val calendar = Calendar.getInstance()
        val startOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        return getHabitRecords(userId, startOfDay, endOfDay)
    }

    suspend fun getWeeklyStats(userId: String): Map<HabitType, Double> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekAgo = calendar.time
        val now = Date()

        val records = getHabitRecords(userId, weekAgo, now)

        return records.groupBy { it.type }
            .mapValues { (_, records) ->
                records.sumOf { it.value } / records.size.coerceAtLeast(1)
            }
    }

    suspend fun getWeeklySummary(userId: String): Map<String, Any> {
        return try {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startDate = calendar.time

            val records = getHabitRecords(userId, startDate, endDate)

            //Calcular promedios semanales
            val waterRecords = records.filter { it.type == HabitType.WATER }
            val sleepRecords = records.filter { it.type == HabitType.SLEEP }
            val stepsRecords = records.filter { it.type == HabitType.STEPS }
            val exerciseRecords = records.filter { it.type == HabitType.EXERCISE }

            val weeklyStats = mapOf(
                "avgWater" to waterRecords.map { it.value }.average(),
                "avgSleep" to sleepRecords.map { it.value }.average(),
                "totalSteps" to stepsRecords.map { it.value }.sum().toInt(),
                "avgExercise" to exerciseRecords.map { it.value }.average(),
                "streakDays" to calculateStreak(userId)
            )

            weeklyStats
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun calculateStreak(userId: String): Int {
        //Implementación simple de racha - contar días consecutivos con al menos un hábito
        var streak = 0
        val calendar = Calendar.getInstance()

        for (i in 0 until 30) { //Revisar hasta 30 días
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            val dayRecords = getHabitRecords(userId, dayStart, dayEnd)
            if (dayRecords.isNotEmpty()) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}