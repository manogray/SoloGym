package com.example.sologym.model

import com.example.sologym.database.entity.Player
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerProgressionTest {
    @Test
    fun resetRestoresInitialProgressAndStartsFailureEvaluationToday() {
        val today = LocalDate.of(2026, 8, 31)

        val result = PlayerProgression.reset(today)

        assertEquals(1, result.level)
        assertEquals(0, result.experienciaAtual)
        assertEquals(100, result.experienciaMaxima)
        assertEquals(0, result.streakTreinos)
        assertEquals(0, result.falhasTreino)
        assertEquals(today, result.proximaDataFalha)
    }

    @Test
    fun completedWorkoutAddsExperienceAndStreak() {
        val result = PlayerProgression.completedWorkout(Player())

        assertEquals(30, result.experienciaAtual)
        assertEquals(1, result.streakTreinos)
        assertEquals(1, result.level)
    }

    @Test
    fun voluntaryWorkoutAddsTwentyExperienceWithoutChangingStreak() {
        val result = PlayerProgression.completedVoluntaryWorkout(
            Player(experienciaAtual = 10, streakTreinos = 4)
        )

        assertEquals(30, result.experienciaAtual)
        assertEquals(4, result.streakTreinos)
    }

    @Test
    fun completedRestDayAddsFifteenExperienceWithoutChangingStreak() {
        val result = PlayerProgression.completedRestDay(
            Player(experienciaAtual = 10, streakTreinos = 4)
        )

        assertEquals(25, result.experienciaAtual)
        assertEquals(4, result.streakTreinos)
    }

    @Test
    fun reachingMaximumExperienceLevelsUpAndIncreasesTarget() {
        val player = Player(experienciaAtual = 80, streakTreinos = 4)

        val result = PlayerProgression.completedWorkout(player)

        assertEquals(2, result.level)
        assertEquals(0, result.experienciaAtual)
        assertEquals(120, result.experienciaMaxima)
        assertEquals(5, result.streakTreinos)
    }

    @Test
    fun missedWorkoutRemovesExperienceResetsStreakAndAddsFailure() {
        val player = Player(experienciaAtual = 30, streakTreinos = 3, falhasTreino = 2)

        val result = PlayerProgression.missedWorkout(player)

        assertEquals(20, result.experienciaAtual)
        assertEquals(0, result.streakTreinos)
        assertEquals(3, result.falhasTreino)
    }

    @Test
    fun missedWorkoutNeverMakesExperienceNegative() {
        val result = PlayerProgression.missedWorkout(Player(experienciaAtual = 5))

        assertEquals(0, result.experienciaAtual)
        assertEquals(1, result.level)
        assertEquals(100, result.experienciaMaxima)
    }

    @Test
    fun missedWorkoutWithInsufficientExperienceLowersLevel() {
        val player = Player(
            level = 3,
            experienciaAtual = 5,
            experienciaMaxima = 144,
            streakTreinos = 8
        )

        val result = PlayerProgression.missedWorkout(player)

        assertEquals(2, result.level)
        assertEquals(0, result.experienciaAtual)
        assertEquals(115, result.experienciaMaxima)
        assertEquals(0, result.streakTreinos)
        assertEquals(1, result.falhasTreino)
    }

    @Test
    fun repeatedFailuresCanReturnPlayerToLevelOne() {
        var player = Player(
            level = 20,
            experienciaAtual = 0,
            experienciaMaxima = 3195,
            streakTreinos = 30
        )

        repeat(30) {
            player = PlayerProgression.missedWorkout(player)
        }

        assertEquals(1, player.level)
        assertEquals(0, player.experienciaAtual)
        assertEquals(100, player.experienciaMaxima)
        assertEquals(0, player.streakTreinos)
        assertEquals(30, player.falhasTreino)
    }
}
