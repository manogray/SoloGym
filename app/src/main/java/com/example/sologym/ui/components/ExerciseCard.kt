package com.example.sologym.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.*
import androidx.compose.ui.text.style.TextDecoration
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.Serie

@Composable
fun ExerciseCard(
    exercicio: Exercicio,
    series: List<Serie>,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit,
    onSubstitutesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                LightBlue
            else 
                DarkBlue
        ),
        border = BorderStroke(1.dp, FullWhite),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercicio.nome,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isCompleted) 
                        TextDecoration.LineThrough
                    else null
                )
                Text(
                    text = "DESCANSO: ${exercicio.descansoSegundos}s",
                    style = MaterialTheme.typography.bodySmall
                )
                series.sortedBy { it.ordem }.forEach { serie ->
                    Text(
                        text = "SÉRIE ${serie.ordem}: ${serie.repeticoes} REPS • ${serie.carga.formatCarga()} KG",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onSubstitutesClick) {
                    Text("SUBSTITUIÇÕES", color = FullWhite, textDecoration = TextDecoration.Underline)
                }
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted() }
                )
            }
        }
    }
}

private fun Double.formatCarga(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()
