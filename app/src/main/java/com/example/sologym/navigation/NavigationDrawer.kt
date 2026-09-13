package com.example.sologym.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.sologym.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.*

sealed class DrawerNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    object Home : DrawerNavItem(
        "DIÁRIA",
        Icons.Default.NewReleases,
        Icons.Outlined.NewReleases,
        Screen.Home.route
    )
    object Workouts : DrawerNavItem(
        "TREINOS",
        Icons.AutoMirrored.Filled.Assignment,
        Icons.AutoMirrored.Outlined.Assignment,
        Screen.Workouts.route
    )
    object Exercises : DrawerNavItem(
        "EXERCÍCIOS",
        Icons.Default.FitnessCenter,
        Icons.Outlined.FitnessCenter,
        Screen.Exercises.route
    )
    object Statistics : DrawerNavItem(
        "JOGADOR",
        Icons.Default.PersonPin,
        Icons.Outlined.PersonPin,
        Screen.Statistics.route
    )
    object Information : DrawerNavItem(
        "INFORMAÇÕES",
        Icons.Default.Info,
        Icons.Outlined.Info,
        Screen.Information.route
    )
}

@Composable
fun SoloGymNavigationDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        DrawerNavItem.Home,
        DrawerNavItem.Workouts,
        DrawerNavItem.Exercises,
        DrawerNavItem.Statistics,
        DrawerNavItem.Information
    )

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = DarkBlue,
        drawerContentColor = FullWhite,
    ) {
        Image(
            painter = painterResource(R.drawable.solo_gym_logo),
            contentDescription = "Solo Gym",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )
        HorizontalDivider(color = LightBlue)

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationDrawerItem(
                icon = { Icon(imageVector = if (selected) {
                    item.selectedIcon
                } else {
                    item.unselectedIcon
                }, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selected,
                onClick = { onNavigate(item.route) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = LightBlue,
                    selectedIconColor = FullWhite,
                    selectedTextColor = FullWhite,
                    unselectedIconColor = FullWhite,
                    unselectedTextColor = FullWhite,
                ),
            )
        }
    }
}
