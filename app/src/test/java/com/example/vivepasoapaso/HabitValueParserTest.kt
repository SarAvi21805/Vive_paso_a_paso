package com.example.vivepasoapaso

import com.example.vivepasoapaso.ui.screens.registerhabit.parseHabitValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HabitValueParserTest {

    @Test
    fun parseHabitValue_acceptsInteger() {
        assertEquals(42.0, requireNotNull(parseHabitValue("42")), 0.0)
    }

    @Test
    fun parseHabitValue_acceptsDecimalWithDot() {
        assertEquals(2.5, requireNotNull(parseHabitValue("2.5")), 0.0)
    }

    @Test
    fun parseHabitValue_rejectsEmptyInput() {
        assertNull(parseHabitValue(""))
    }

    @Test
    fun parseHabitValue_rejectsWhitespaceOnlyInput() {
        assertNull(parseHabitValue("   "))
    }

    @Test
    fun parseHabitValue_rejectsNonNumericText() {
        assertNull(parseHabitValue("mucho"))
    }

    @Test
    fun parseHabitValue_preservesZeroAsValid() {
        assertEquals(0.0, requireNotNull(parseHabitValue("0")), 0.0)
    }

    @Test
    fun parseHabitValue_preservesNegativeNumbersAsValid() {
        assertEquals(-3.5, requireNotNull(parseHabitValue("-3.5")), 0.0)
    }

    @Test
    fun parseHabitValue_rejectsCommaDecimal() {
        assertNull(parseHabitValue("2,5"))
    }
}
