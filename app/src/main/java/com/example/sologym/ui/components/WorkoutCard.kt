package com.example.sologym.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.*
import com.example.sologym.database.entity.Treino
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutCard(
    treino: Treino,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        ),
        shape = RectangleShape,
        border = BorderStroke(1.dp, FullWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = treino.diaSemana.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault())
                        .uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "STATUS: ${treino.status.displayName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir Treino",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
