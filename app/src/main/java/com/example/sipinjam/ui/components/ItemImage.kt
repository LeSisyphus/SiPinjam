package com.example.sipinjam.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ItemImage(
    fotoUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    CloudinaryImage(
        imageUrl = fotoUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        shape = RoundedCornerShape(12.dp),
    )
}
