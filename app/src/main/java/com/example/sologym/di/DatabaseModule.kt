package com.example.sologym.di

import android.content.Context
import androidx.room.Room
import com.example.sologym.database.SoloGymDatabase
import com.example.sologym.database.dao.*
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
    fun provideDatabase(@ApplicationContext context: Context): SoloGymDatabase {
        return Room.databaseBuilder(
            context,
            SoloGymDatabase::class.java,
            "solo_gym.db"
        )
            .addMigrations(
                SoloGymDatabase.MIGRATION_1_2,
                SoloGymDatabase.MIGRATION_2_3
            )
            .build()
    }

    @Provides
    fun provideExerciseDao(db: SoloGymDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideSeriesDao(db: SoloGymDatabase): SeriesDao = db.seriesDao()

    @Provides
    fun provideWorkoutDao(db: SoloGymDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideWorkoutExerciseDao(db: SoloGymDatabase): WorkoutExerciseDao = db.workoutExerciseDao()

    @Provides
    fun provideSubstituteDao(db: SoloGymDatabase): SubstituteDao = db.substituteDao()

    @Provides
    fun provideHistoryDao(db: SoloGymDatabase): HistoryDao = db.historyDao()

    @Provides
    fun providePlayerDao(db: SoloGymDatabase): PlayerDao = db.playerDao()
}
