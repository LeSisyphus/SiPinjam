package com.example.sipinjam.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.sipinjam.data.preferences.AppPreferences
import java.util.Locale

@Composable
fun localizedStringResource(
    @StringRes id: Int,
    vararg formatArgs: Any,
): String {
    val context = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val languageCode = AppPreferences.languageCode

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

    return if (formatArgs.isEmpty()) {
        localizedContext.getString(id)
    } else {
        localizedContext.getString(id, *formatArgs)
    }
}
