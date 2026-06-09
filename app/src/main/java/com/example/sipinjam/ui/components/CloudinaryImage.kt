package com.example.sipinjam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.SiPinjamBlue

@Composable
fun CloudinaryImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(12.dp),
    placeholderSize: Dp = 28.dp,
) {
    val safeUrl = imageUrl?.takeIf { it.isNotBlank() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(DarkImageBg),
        contentAlignment = Alignment.Center,
    ) {
        if (safeUrl != null) {
            AsyncImage(
                model = safeUrl,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = contentDescription,
                tint = SiPinjamBlue,
                modifier = Modifier.size(placeholderSize),
            )
        }
    }
}
