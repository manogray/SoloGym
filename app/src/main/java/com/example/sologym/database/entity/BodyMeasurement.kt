package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "body_measurement")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordedAt: LocalDateTime,
    val pesoKg: Double,
    val alturaCm: Double,
)
