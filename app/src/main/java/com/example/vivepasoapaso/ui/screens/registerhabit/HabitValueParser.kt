package com.example.vivepasoapaso.ui.screens.registerhabit

/**
 * Converts the numeric value entered for a non-food habit.
 *
 * The existing form used Kotlin's Double.toDoubleOrNull(), so decimal values
 * use a dot. Surrounding whitespace is ignored, while a comma is not treated
 * as a decimal separator. Zero and negative values remain valid numeric input;
 * this function does not impose business limits.
 */
fun parseHabitValue(input: String): Double? = input.trim().toDoubleOrNull()
