package com.example.sologym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.*

@Composable
fun SoloTopBar(
    title: String,
    icon: ImageVector,
    onMenuClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DarkBlue)
            .drawBehind {
                val stroke = 3.dp.toPx()

                drawLine(
                    color = LightBlue,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = stroke
                )

                drawLine(
                    color = LightBlue,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = stroke
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (onMenuClick != null) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu",
                    tint = FullWhite,
                )
            }
        }
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = FullWhite
            )
            Text(title)
        }
    }
}
