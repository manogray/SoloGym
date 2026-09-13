package com.example.sologym.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
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
import com.example.sologym.ui.components.ProfilePhoto
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.BorderStroke
import com.example.sologym.ui.theme.*
import com.example.sologym.database.entity.BodyMeasurement
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissResetDialog,
            title = { Text("RESETAR PROGRESSO?") },
            text = {
                Text(
                    "LEVEL, XP, STREAK E FALHAS SERÃO ZERADOS. " +
                        "TODO O HISTÓRICO DE TREINOS TAMBÉM SERÁ APAGADO. " +
                        "EXERCÍCIOS E TREINOS PROGRAMADOS SERÃO MANTIDOS."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::resetPlayerProgress,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissResetDialog) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SoloTopBar(
                imageRes = com.example.sologym.R.drawable.player,
                onMenuClick = onOpenDrawer,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileInformationCard(
                name = uiState.profile.nome,
                age = uiState.age,
                weightKg = uiState.measurements.lastOrNull()?.pesoKg,
                heightCm = uiState.measurements.lastOrNull()?.alturaCm,
                photoUri = uiState.profile.fotoUri,
            )
            PlayerCard(
                level = uiState.player.level,
                currentExperience = uiState.player.experienciaAtual,
                maximumExperience = uiState.player.experienciaMaxima,
                streak = uiState.player.streakTreinos,
                failures = uiState.player.falhasTreino
            )
            OutlinedButton(
                onClick = viewModel::showResetDialog,
                enabled = !uiState.isResetting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                shape = RectangleShape
            ) {
                if (uiState.isResetting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Reset")
                }
            }
            if (uiState.measurements.isNotEmpty()) {
                MeasurementHistoryCard(measurements = uiState.measurements)
            }
            StatisticCard("TREINOS ESSA SEMANA", uiState.workoutsThisWeek.toString())
            StatisticCard("TREINOS ESSE MÊS", uiState.workoutsThisMonth.toString())
            StatisticCard("TREINOS ESSE ANO", uiState.workoutsThisYear.toString())
        }
    }
}

@Composable
private fun ProfileInformationCard(
    name: String,
    age: Int?,
    weightKg: Double?,
    heightCm: Double?,
    photoUri: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        border = BorderStroke(1.dp, FullWhite),
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfilePhoto(photoUri = photoUri, size = 88.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = name.ifBlank { "PERFIL NÃO PREENCHIDO" },
                    style = MaterialTheme.typography.titleLarge,
                )
                age?.let { Text("IDADE: $it ANOS") }
                weightKg?.let { Text("PESO: ${formatMeasurement(it)} KG") }
                heightCm?.let { Text("ALTURA: ${formatMeasurement(it)} CM") }
            }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        ),
        border = BorderStroke(1.dp, FullWhite),
        shape = RectangleShape,
    ){
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("JOGADOR", style = MaterialTheme.typography.labelMedium)
            Text("LEVEL $level", style = MaterialTheme.typography.headlineLarge)
            Text("XP: $currentExperience / $maximumExperience")
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = LightBlue,
                trackColor = FullWhite,
                drawStopIndicator = {}
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

private fun formatMeasurement(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

@Composable
private fun MeasurementHistoryCard(measurements: List<BodyMeasurement>) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        border = BorderStroke(1.dp, FullWhite),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("ÚLTIMAS MEDIÇÕES", style = MaterialTheme.typography.titleMedium)
            MeasurementLineChart(
                title = "PESO (KG)",
                values = measurements.map { it.pesoKg },
                lineColor = LightBlue,
            )
            MeasurementLineChart(
                title = "ALTURA (CM)",
                values = measurements.map { it.alturaCm },
                lineColor = FullWhite,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(measurements.first().recordedAt.format(dateFormatter))
                if (measurements.size > 1) {
                    Text(measurements.last().recordedAt.format(dateFormatter))
                }
            }
        }
    }
}

@Composable
private fun MeasurementLineChart(
    title: String,
    values: List<Double>,
    lineColor: Color,
) {
    val minimum = values.minOrNull() ?: 0.0
    val maximum = values.maxOrNull() ?: 0.0
    val range = (maximum - minimum).takeIf { it > 0.0 } ?: 1.0

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text("${formatMeasurement(values.last())} ATUAL")
        }
        Canvas(modifier = Modifier.fillMaxWidth().height(96.dp)) {
            val horizontalPadding = 8.dp.toPx()
            val verticalPadding = 8.dp.toPx()
            val chartWidth = size.width - horizontalPadding * 2
            val chartHeight = size.height - verticalPadding * 2
            val points = values.mapIndexed { index, value ->
                val x = if (values.size == 1) {
                    size.width / 2
                } else {
                    horizontalPadding + chartWidth * index / (values.size - 1)
                }
                val normalized = ((value - minimum) / range).toFloat()
                val y = verticalPadding + chartHeight * (1f - normalized)
                androidx.compose.ui.geometry.Offset(x, y)
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            points.forEach { point ->
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = point)
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        ),
        border = BorderStroke(1.dp, FullWhite),
        shape = RectangleShape,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
