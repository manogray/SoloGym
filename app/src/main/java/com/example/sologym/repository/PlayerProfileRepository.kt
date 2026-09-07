package com.example.sologym.repository

import androidx.room.withTransaction
import com.example.sologym.database.SoloGymDatabase
import com.example.sologym.database.dao.BodyMeasurementDao
import com.example.sologym.database.dao.PlayerProfileDao
import com.example.sologym.database.entity.BodyMeasurement
import com.example.sologym.database.entity.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerProfileRepository @Inject constructor(
    private val database: SoloGymDatabase,
    private val profileDao: PlayerProfileDao,
    private val measurementDao: BodyMeasurementDao,
) {
    fun observeProfile(): Flow<PlayerProfile> =
        profileDao.observeProfile().map { it ?: PlayerProfile() }

    fun observeLatestMeasurement(): Flow<BodyMeasurement?> = measurementDao.observeLatest()

    fun observeRecentMeasurements(limit: Int = 6): Flow<List<BodyMeasurement>> =
        measurementDao.observeRecent(limit)

    suspend fun save(profile: PlayerProfile, measurement: BodyMeasurement?) {
        database.withTransaction {
            profileDao.save(profile)
            measurement?.let { measurementDao.insert(it) }
        }
    }
}
