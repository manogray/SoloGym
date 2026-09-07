package com.example.sologym.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Workouts : Screen("workouts")
    object Exercises : Screen("exercises")
    object Statistics : Screen("statistics")
    object Information : Screen("information")
    object ExerciseForm : Screen("exercise_form?id={id}") {
        fun createRoute(id: Long? = null) = if (id != null) "exercise_form?id=$id" else "exercise_form"
    }
    object WorkoutForm : Screen("workout_form?id={id}") {
        fun createRoute(id: Long? = null) = if (id != null) "workout_form?id=$id" else "workout_form"
    }
}
