package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "historico_treino",
    foreignKeys = [
        ForeignKey(
            entity = Treino::class,
            parentColumns = ["id"],
            childColumns = ["treinoId"],
            onDelete = ForeignKey.SET_NULL // History remains if workout is deleted
        )
    ],
    indices = [Index(value = ["treinoId"])]
)
data class HistoricoTreino(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val treinoId: Long?,
    val data: LocalDateTime,
    val duracaoSegundos: Long
)
