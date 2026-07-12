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
        Player::class
    ],
    version = 3,
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
    }
}
