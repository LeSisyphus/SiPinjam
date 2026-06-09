package com.example.sipinjam.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun SiPinjamLocale(
    languageCode: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val localizedContext = remember(context, languageCode) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        content()
    }
}
