package com.example.vivepasoapaso.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTOs para Edamam
@JsonClass(generateAdapter = true)
data class EdamamResponse(
    @Json(name = "totalNutrients") val totalNutrients: Nutrients
)

@JsonClass(generateAdapter = true)
data class Nutrients(
    @Json(name = "ENERC_KCAL") val calories: NutrientInfo
)

@JsonClass(generateAdapter = true)
data class NutrientInfo(
    @Json(name = "label") val label: String,
    @Json(name = "quantity") val quantity: Double,
    @Json(name = "unit") val unit: String
)


// DTOs para OpenWeather
@JsonClass(generateAdapter = true)
data class OpenWeatherResponse(
    @Json(name = "weather") val weather: List<Weather>,
    @Json(name = "main") val main: Main
)

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "temp") val temp: Double
)