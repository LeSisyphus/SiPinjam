package com.example.sipinjam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.example.sipinjam.data.preferences.AppPreferences
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.navigation.NavGraph
import com.example.sipinjam.ui.theme.SiPinjamLocale
import com.example.sipinjam.ui.theme.SiPinjamTheme

class MainActivity : ComponentActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCloudinary()
        AppPreferences.load(this)

        enableEdgeToEdge()

        val activityResultRegistryOwner = this

        setContent {
            CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides activityResultRegistryOwner
            ) {
                SiPinjamTheme(darkTheme = AppPreferences.isDarkMode) {
                    SiPinjamLocale(languageCode = AppPreferences.languageCode) {
                        val navController = rememberNavController()

                        var isLoggedIn by rememberSaveable { mutableStateOf(false) }
                        var isAdmin by rememberSaveable { mutableStateOf(false) }
                        var isReady by rememberSaveable { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            if (authRepository.isLoggedIn()) {
                                val user = authRepository.getCurrentUser()

                                isLoggedIn = user != null
                                isAdmin = user?.role == "admin"
                            } else {
                                isLoggedIn = false
                                isAdmin = false
                            }

                            isReady = true
                        }

                        if (isReady) {
                            NavGraph(
                                navController = navController,
                                isLoggedIn = isLoggedIn,
                                isAdmin = isAdmin,
                                onAuthStateChanged = { loggedIn, admin ->
                                    isLoggedIn = loggedIn
                                    isAdmin = admin
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initCloudinary() {
        if (isCloudinaryInitialized) return

        val alreadyInitialized = runCatching {
            MediaManager.get()
        }.isSuccess

        if (alreadyInitialized) {
            isCloudinaryInitialized = true
            return
        }

        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
        )

        runCatching {
            MediaManager.init(applicationContext, config)
            isCloudinaryInitialized = true
        }.onFailure { throwable ->
            val errorMessage = throwable.message.orEmpty()

            val isAlreadyInitializedError =
                errorMessage.contains("already initialized", ignoreCase = true) ||
                        errorMessage.contains("already", ignoreCase = true) &&
                        errorMessage.contains("initialized", ignoreCase = true)

            if (isAlreadyInitializedError) {
                isCloudinaryInitialized = true
            } else {
                throw throwable
            }
        }
    }

    companion object {
        private var isCloudinaryInitialized = false
    }
}