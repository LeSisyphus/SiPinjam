package com.example.sipinjam.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sipinjam.ui.theme.StatusRed

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        color = StatusRed,
        modifier = modifier,
    )
}
