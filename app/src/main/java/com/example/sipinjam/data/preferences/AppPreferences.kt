package com.example.sipinjam.data.preferences

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

object AppPreferences {
    private const val PREF_NAME = "sipinjam_app_preferences"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "language"

    var isDarkMode by mutableStateOf(false)
        private set

    var languageCode by mutableStateOf("id")
        private set

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        languageCode = prefs.getString(KEY_LANGUAGE, "id").orEmpty().ifBlank { "id" }
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        isDarkMode = enabled
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    fun setLanguage(context: Context, language: String) {
        val normalized = when (language.lowercase(Locale.ROOT)) {
            "en" -> "en"
            else -> "id"
        }

        languageCode = normalized
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, normalized)
            .apply()
    }
}
