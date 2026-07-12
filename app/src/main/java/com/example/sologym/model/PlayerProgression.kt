package com.example.sologym.model

import com.example.sologym.database.entity.Player
import kotlin.math.ceil
import kotlin.math.roundToInt

object PlayerProgression {
    const val COMPLETED_WORKOUT_XP = 20
    const val MISSED_WORKOUT_XP = 10
    private const val LEVEL_XP_MULTIPLIER = 1.2

    fun completedWorkout(player: Player): Player {
        val earnedExperience = player.experienciaAtual + COMPLETED_WORKOUT_XP
        val leveledUp = earnedExperience >= player.experienciaMaxima

        return player.copy(
            level = if (leveledUp) player.level + 1 else player.level,
            experienciaAtual = if (leveledUp) 0 else earnedExperience,
            experienciaMaxima = if (leveledUp) {
                ceil(player.experienciaMaxima * LEVEL_XP_MULTIPLIER).toInt()
            } else {
                player.experienciaMaxima
            },
            streakTreinos = player.streakTreinos + 1
        )
    }

    fun missedWorkout(player: Player): Player {
        val losesLevel = player.level > 1 && player.experienciaAtual < MISSED_WORKOUT_XP
        val newLevel = if (losesLevel) player.level - 1 else player.level

        return player.copy(
            level = newLevel,
            experienciaAtual = if (losesLevel) {
                0
            } else {
                (player.experienciaAtual - MISSED_WORKOUT_XP).coerceAtLeast(0)
            },
            experienciaMaxima = if (losesLevel) {
                if (newLevel == 1) INITIAL_MAXIMUM_XP else {
                    (player.experienciaMaxima * LEVEL_DOWN_XP_MULTIPLIER).roundToInt()
                }
            } else {
                player.experienciaMaxima
            },
            streakTreinos = 0,
            falhasTreino = player.falhasTreino + 1
        )
    }

    private const val INITIAL_MAXIMUM_XP = 100
    private const val LEVEL_DOWN_XP_MULTIPLIER = 0.8
}
