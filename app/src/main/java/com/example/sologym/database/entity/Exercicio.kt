package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.sologym.model.ExerciseType

@Entity(tableName = "exercicio")
data class Exercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descansoSegundos: Int,
    @ColumnInfo(defaultValue = "'STRENGTH'")
    val tipo: ExerciseType = ExerciseType.STRENGTH,
    val duracaoMinutos: Int? = null,
)
