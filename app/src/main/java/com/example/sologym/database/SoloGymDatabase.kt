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
        ActiveWorkoutSession::class,
        PlayerProfile::class,
        BodyMeasurement::class
    ],
    version = 7,
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
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao

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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS player_profile (
                        id INTEGER NOT NULL,
                        nome TEXT NOT NULL,
                        dataNascimento TEXT,
                        pesoKg REAL,
                        alturaCm REAL,
                        fotoUri TEXT,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS body_measurement (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recordedAt TEXT NOT NULL,
                        pesoKg REAL NOT NULL,
                        alturaCm REAL NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO body_measurement (recordedAt, pesoKg, alturaCm)
                    SELECT strftime('%Y-%m-%dT%H:%M:%S', 'now'), pesoKg, alturaCm
                    FROM player_profile
                    WHERE id = 1 AND pesoKg IS NOT NULL AND alturaCm IS NOT NULL
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE player_profile_new (
                        id INTEGER NOT NULL,
                        nome TEXT NOT NULL,
                        dataNascimento TEXT,
                        fotoUri TEXT,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO player_profile_new (id, nome, dataNascimento, fotoUri)
                    SELECT id, nome, dataNascimento, fotoUri FROM player_profile
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE player_profile")
                db.execSQL("ALTER TABLE player_profile_new RENAME TO player_profile")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE player_profile_new (
                        id INTEGER NOT NULL,
                        nome TEXT NOT NULL,
                        dataNascimento TEXT,
                        fotoUri TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO player_profile_new (id, nome, dataNascimento, fotoUri)
                    SELECT id, nome, dataNascimento,
                        CASE
                            WHEN fotoUri IN (
                                'bellion_profile', 'beru_profile', 'igris_profile',
                                'iron_profile', 'tank_profile', 'default_profile'
                            ) THEN fotoUri
                            ELSE 'default_profile'
                        END
                    FROM player_profile
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE player_profile")
                db.execSQL("ALTER TABLE player_profile_new RENAME TO player_profile")
            }
        }
    }
}
