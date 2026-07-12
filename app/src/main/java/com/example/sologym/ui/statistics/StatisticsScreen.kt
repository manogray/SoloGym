package com.example.sologym.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sologym.ui.components.SoloTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SoloTopBar("ESTATÍSTICAS", Icons.Outlined.Analytics)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PlayerCard(
                level = uiState.player.level,
                currentExperience = uiState.player.experienciaAtual,
                maximumExperience = uiState.player.experienciaMaxima,
                streak = uiState.player.streakTreinos,
                failures = uiState.player.falhasTreino
            )
            StatisticCard("TREINOS ESSA SEMANA", uiState.workoutsThisWeek.toString())
            StatisticCard("TREINOS ESSE MÊS", uiState.workoutsThisMonth.toString())
            StatisticCard("TREINOS ESSE ANO", uiState.workoutsThisYear.toString())
        }
    }
}

@Composable
private fun PlayerCard(
    level: Int,
    currentExperience: Int,
    maximumExperience: Int,
    streak: Int,
    failures: Int
) {
    val progress = if (maximumExperience > 0) {
        currentExperience.toFloat() / maximumExperience.toFloat()
    } else {
        0f
    }

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("PLAYER", style = MaterialTheme.typography.labelMedium)
            Text("LEVEL $level", style = MaterialTheme.typography.headlineLarge)
            Text("XP: $currentExperience / $maximumExperience")
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("STREAK: $streak")
                Text("FALHAS: $failures")
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
