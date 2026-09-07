package com.example.sologym.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sologym.ui.exercises.ExerciseFormScreen
import com.example.sologym.ui.exercises.ExerciseScreen
import com.example.sologym.ui.home.HomeScreen
import com.example.sologym.ui.statistics.StatisticsScreen
import com.example.sologym.ui.information.InformationScreen
import com.example.sologym.ui.workouts.WorkoutFormScreen
import com.example.sologym.ui.workouts.WorkoutScreen

@Composable
fun SoloGymNavHost(
    navController: NavHostController,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onOpenDrawer = onOpenDrawer)
        }
        composable(Screen.Workouts.route) {
            WorkoutScreen(
                onOpenDrawer = onOpenDrawer,
                onAddWorkout = { navController.navigate(Screen.WorkoutForm.createRoute()) },
                onEditWorkout = { id -> navController.navigate(Screen.WorkoutForm.createRoute(id)) }
            )
        }
        composable(
            route = Screen.WorkoutForm.route,
            arguments = listOf(navArgument("id") { nullable = true })
        ) {
            WorkoutFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Exercises.route) {
            ExerciseScreen(
                onOpenDrawer = onOpenDrawer,
                onAddExercise = { navController.navigate(Screen.ExerciseForm.createRoute()) },
                onEditExercise = { id -> navController.navigate(Screen.ExerciseForm.createRoute(id)) }
            )
        }
        composable(
            route = Screen.ExerciseForm.route,
            arguments = listOf(navArgument("id") { nullable = true })
        ) {
            ExerciseFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(onOpenDrawer = onOpenDrawer)
        }
        composable(Screen.Information.route) {
            InformationScreen(onOpenDrawer = onOpenDrawer)
        }
    }
}
