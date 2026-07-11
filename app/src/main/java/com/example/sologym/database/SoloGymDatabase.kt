package com.example.sologym.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sologym.database.converter.Converters
import com.example.sologym.database.dao.*
import com.example.sologym.database.entity.*

@Database(
    entities = [
        Exercicio::class,
        Serie::class,
        Treino::class,
        TreinoExercicio::class,
        ExercicioSubstituto::class,
        HistoricoTreino::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SoloGymDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun seriesDao(): SeriesDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun substituteDao(): SubstituteDao
    abstract fun historyDao(): HistoryDao
}
