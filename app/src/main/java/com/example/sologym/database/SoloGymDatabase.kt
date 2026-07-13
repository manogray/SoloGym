package com.example.sologym.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        HistoricoTreino::class,
        Player::class,
        ActiveWorkoutSession::class
    ],
    version = 4,
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
    abstract fun playerDao(): PlayerDao
    abstract fun activeWorkoutSessionDao(): ActiveWorkoutSessionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE serie ADD COLUMN carga REAL NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS player (
                        id INTEGER NOT NULL,
                        level INTEGER NOT NULL,
                        experienciaAtual INTEGER NOT NULL,
                        experienciaMaxima INTEGER NOT NULL,
                        streakTreinos INTEGER NOT NULL,
                        falhasTreino INTEGER NOT NULL,
                        proximaDataFalha TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS active_workout_session (
                        id INTEGER NOT NULL,
                        workoutId INTEGER NOT NULL,
                        startedAtEpochMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
