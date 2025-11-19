// Dependency Injection

package com.example.vivepasoapaso.di

import com.example.vivepasoapaso.data.remote.EdamamApiService
import com.example.vivepasoapaso.data.remote.OpenWeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideEdamamApiService(moshi: Moshi): EdamamApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.edamam.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(EdamamApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenWeatherApiService(moshi: Moshi): OpenWeatherApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenWeatherApiService::class.java)
    }
}