package com.example.sipinjam.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun SiPinjamLocale(
    languageCode: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val baseConfiguration = LocalConfiguration.current

    val normalizedLanguageCode = remember(languageCode) {
        when (languageCode.lowercase(Locale.ROOT)) {
            "en", "english" -> "en"
            "id", "in", "indonesia", "indonesian" -> "id"
            else -> "id"
        }
    }

    val locale = remember(normalizedLanguageCode) {
        Locale.forLanguageTag(normalizedLanguageCode)
    }

    val localizedConfiguration = remember(baseConfiguration, normalizedLanguageCode) {
        Configuration(baseConfiguration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    val localizedContext = remember(context, localizedConfiguration) {
        context.createConfigurationContext(localizedConfiguration)
    }

    SideEffect {
        Locale.setDefault(locale)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
    ) {
        content()
    }
}