/*package com.example.vivepasoapaso.di

import com.example.vivepasoapaso.data.repository.AuthRepository
import com.example.vivepasoapaso.data.repository.ChatRepository
import com.example.vivepasoapaso.data.repository.ChatRepositoryImpl
import com.example.vivepasoapaso.data.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(): ChatRepository {
        return ChatRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRepository: AuthRepository
    ): AuthRepository {
        return authRepository
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitRepository: HabitRepository
    ): HabitRepository {
        return habitRepository
    }
}*/