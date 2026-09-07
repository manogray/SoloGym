package com.example.sologym.model

import com.example.sologym.database.entity.Player
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.roundToInt

object PlayerProgression {
    const val COMPLETED_WORKOUT_XP = 30
    const val VOLUNTARY_WORKOUT_XP = 20
    const val REST_DAY_XP = 15
    const val MISSED_WORKOUT_XP = 10
    private const val LEVEL_XP_MULTIPLIER = 1.2

    fun reset(today: LocalDate = LocalDate.now()): Player =
        Player(proximaDataFalha = today)

    fun completedWorkout(player: Player): Player {
        return awardExperience(player, COMPLETED_WORKOUT_XP, incrementStreak = true)
    }

    fun completedVoluntaryWorkout(player: Player): Player {
        return awardExperience(player, VOLUNTARY_WORKOUT_XP, incrementStreak = false)
    }

    fun completedRestDay(player: Player): Player {
        return awardExperience(player, REST_DAY_XP, incrementStreak = false)
    }

    private fun awardExperience(
        player: Player,
        experience: Int,
        incrementStreak: Boolean
    ): Player {
        val earnedExperience = player.experienciaAtual + experience
        val leveledUp = earnedExperience >= player.experienciaMaxima

        return player.copy(
            level = if (leveledUp) player.level + 1 else player.level,
            experienciaAtual = if (leveledUp) 0 else earnedExperience,
            experienciaMaxima = if (leveledUp) {
                ceil(player.experienciaMaxima * LEVEL_XP_MULTIPLIER).toInt()
            } else {
                player.experienciaMaxima
            },
            streakTreinos = if (incrementStreak) {
                player.streakTreinos + 1
            } else {
                player.streakTreinos
            }
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
