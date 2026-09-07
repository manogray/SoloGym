package com.example.sologym.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sologym.database.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    @Insert
    suspend fun insert(measurement: BodyMeasurement)

    @Query("SELECT * FROM body_measurement ORDER BY recordedAt DESC LIMIT 1")
    fun observeLatest(): Flow<BodyMeasurement?>

    @Query("SELECT * FROM body_measurement ORDER BY recordedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<BodyMeasurement>>
}
