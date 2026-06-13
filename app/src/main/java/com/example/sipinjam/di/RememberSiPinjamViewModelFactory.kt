package com.example.sipinjam.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.sipinjam.SiPinjamApplication

@Composable
fun rememberSiPinjamViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    return (context.applicationContext as SiPinjamApplication).appContainer.viewModelFactory
}
