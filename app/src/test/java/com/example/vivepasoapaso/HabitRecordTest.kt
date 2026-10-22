package com.example.vivepasoapaso

import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class HabitRecordTest {

    @Test
    fun habitRecord_preservesHabitTypeAndMeasurement() {
        val record = HabitRecord(
            type = HabitType.WATER,
            value = 2.5,
            unit = "litros"
        )

        assertEquals(HabitType.WATER, record.type)
        assertEquals(2.5, record.value, 0.0)
        assertEquals("litros", record.unit)
    }

    @Test
    fun habitRecord_preservesIdentityMetadataAndDates() {
        val recordDate = Timestamp(Date(1_700_000_000_000L))
        val createdAt = Timestamp(Date(1_700_000_100_000L))
        val record = HabitRecord(
            id = "habit-123",
            userId = "user-456",
            type = HabitType.EXERCISE,
            value = 30.0,
            unit = "minutos",
            notes = "Caminata vespertina",
            mood = "GOOD",
            recordDate = recordDate,
            createdAt = createdAt
        )

        assertEquals("habit-123", record.id)
        assertEquals("user-456", record.userId)
        assertEquals(HabitType.EXERCISE, record.type)
        assertEquals(30.0, record.value, 0.0)
        assertEquals("minutos", record.unit)
        assertEquals("Caminata vespertina", record.notes)
        assertEquals("GOOD", record.mood)
        assertEquals(recordDate, record.recordDate)
        assertEquals(createdAt, record.createdAt)
    }
}
