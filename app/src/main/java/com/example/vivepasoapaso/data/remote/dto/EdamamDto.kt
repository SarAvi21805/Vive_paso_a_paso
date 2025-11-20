package com.example.vivepasoapaso.data.remote.dto

import com.squareup.moshi.Json

data class EdamamResponse(
    @field:Json(name = "totalNutrients") val totalNutrients: TotalNutrients
)

data class TotalNutrients(
    @field:Json(name = "ENERC_KCAL") val calories: Nutrient
)

data class Nutrient(
    @field:Json(name = "label") val label: String,
    @field:Json(name = "quantity") val quantity: Double,
    @field:Json(name = "unit") val unit: String
)