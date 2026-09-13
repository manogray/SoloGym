package com.example.sologym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sologym.navigation.DrawerNavItem
import com.example.sologym.navigation.SoloGymNavigationDrawerContent
import com.example.sologym.navigation.SoloGymNavHost
import com.example.sologym.spotify.SpotifyController
import com.example.sologym.ui.theme.SoloGymTheme
import com.example.sologym.ui.theme.DarkBlue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var spotifyController: SpotifyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoloGymTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val mainRoutes = setOf(
                    DrawerNavItem.Home.route,
                    DrawerNavItem.Workouts.route,
                    DrawerNavItem.Exercises.route,
                    DrawerNavItem.Statistics.route,
                    DrawerNavItem.Information.route,
                )

                ModalNavigationDrawer(
                    modifier = Modifier
                        .background(DarkBlue)
                        .statusBarsPadding(),
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute in mainRoutes,
                    drawerContent = {
                        SoloGymNavigationDrawerContent(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                scope.launch { drawerState.close() }
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    },
                ) {
                    SoloGymNavHost(
                        navController = navController,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        spotifyController.handleAuthorizationIntent(this, intent)
    }
}
