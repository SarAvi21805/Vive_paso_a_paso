package com.example.vivepasoapaso.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamApiService {
    @GET("api/nutrition-details")
    suspend fun getNutritionDetails(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") food: String // El alimento a analizar, ej: "1 apple"
    ): EdamamResponse
}

interface OpenWeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Para obtener temp en Celsius
        @Query("lang") lang: String = "es" // Para obtener descripción en español
    ): OpenWeatherResponse
}