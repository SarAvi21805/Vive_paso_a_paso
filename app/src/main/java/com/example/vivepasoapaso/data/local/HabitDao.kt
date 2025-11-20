package com.example.vivepasoapaso.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits ORDER BY date DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE date BETWEEN :startDate AND :endDate")
    fun getHabitsForUserOnDate(startDate: Long, endDate: Long): Flow<List<HabitEntity>>
}