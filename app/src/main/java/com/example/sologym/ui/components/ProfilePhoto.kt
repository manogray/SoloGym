package com.example.sologym.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sologym.ui.theme.LightBlue
import com.example.sologym.model.ProfileImages

@Composable
fun ProfilePhoto(
    photoUri: String?,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(LightBlue),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(ProfileImages.drawableFor(photoUri)),
            contentDescription = "Foto do jogador",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
    }
}
