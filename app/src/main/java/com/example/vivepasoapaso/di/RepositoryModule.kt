package com.example.vivepasoapaso.di

import com.example.vivepasoapaso.data.repository.HabitRepository
import com.example.vivepasoapaso.data.repository.HabitRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        repository: HabitRepository
    ): HabitRepositoryContract
}
