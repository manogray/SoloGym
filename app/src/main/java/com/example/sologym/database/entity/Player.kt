package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "player")
data class Player(
    @PrimaryKey val id: Int = SINGLE_PLAYER_ID,
    val level: Int = 1,
    val experienciaAtual: Int = 0,
    val experienciaMaxima: Int = 100,
    val streakTreinos: Int = 0,
    val falhasTreino: Int = 0,
    val proximaDataFalha: LocalDate = LocalDate.now()
) {
    companion object {
        const val SINGLE_PLAYER_ID = 1
    }
}
