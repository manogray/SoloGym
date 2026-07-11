package com.example.sologym.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.*

sealed class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem(
        "DIÁRIA",
        Icons.Default.NewReleases,
        Icons.Outlined.NewReleases,
        Screen.Home.route
    )
    object Workouts : BottomNavItem(
        "TREINOS",
        Icons.Default.Assignment,
        Icons.Outlined.Assignment,
        Screen.Workouts.route
    )
    object Exercises : BottomNavItem(
        "EXERCÍCIOS",
        Icons.Default.FitnessCenter,
        Icons.Outlined.FitnessCenter,
        Screen.Exercises.route
    )
    object Statistics : BottomNavItem(
        "ESTATÍSTICAS",
        Icons.Default.Analytics,
        Icons.Outlined.Analytics,
        Screen.Statistics.route
    )
}

@Composable
fun SoloGymBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = DarkBlue
    val borderColor = LightBlue

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workouts,
        BottomNavItem.Exercises,
        BottomNavItem.Statistics
    )
    
    NavigationBar(
        containerColor = backgroundColor,
        modifier = modifier
            .drawBehind {
                val strokeWidth = 3.dp.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = { Icon(imageVector = if (selected) {
                    item.selectedIcon
                } else {
                    item.unselectedIcon
                }, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
