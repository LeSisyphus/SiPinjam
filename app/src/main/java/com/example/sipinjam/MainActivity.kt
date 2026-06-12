package com.example.sipinjam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.data.preferences.AppPreferences
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.navigation.NavGraph
import com.example.sipinjam.ui.theme.SiPinjamLocale
import com.example.sipinjam.ui.theme.SiPinjamTheme
import com.example.sipinjam.utils.notification.NotificationHelper

class MainActivity : ComponentActivity() {

    private var isSplashReady = false

    private val authRepository: AuthRepository by lazy {
        (application as SiPinjamApplication).appContainer.authRepository
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isSplashReady }

        super.onCreate(savedInstanceState)

        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        askNotificationPermission()

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
                        var isNavReady by rememberSaveable { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            if (authRepository.isLoggedIn()) {
                                val user = authRepository.getCurrentUser()
                                isLoggedIn = user != null
                                isAdmin = user?.role == "admin"
                            } else {
                                isLoggedIn = false
                                isAdmin = false
                            }

                            isNavReady = true
                            isSplashReady = true
                        }

                        if (isNavReady) {
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

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}