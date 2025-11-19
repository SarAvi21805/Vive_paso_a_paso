package com.example.vivepasoapaso.di

import android.content.Context
import androidx.room.Room
import com.example.vivepasoapaso.data.local.AppDatabase
import com.example.vivepasoapaso.data.local.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vive_database"
        ).build()
    }

    @Provides
    fun provideHabitDao(appDatabase: AppDatabase): HabitDao {
        return appDatabase.habitDao()
    }
}