package com.example.sipinjam.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
fun UserAvatar(
    fotoUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    CloudinaryImage(
        imageUrl = fotoUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        shape = CircleShape,
    )
}
