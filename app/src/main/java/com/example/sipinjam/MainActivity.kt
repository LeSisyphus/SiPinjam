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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.data.preferences.AppPreferences
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.navigation.NavGraph
import com.example.sipinjam.ui.theme.SiPinjamLocale
import com.example.sipinjam.ui.theme.SiPinjamTheme

class MainActivity : ComponentActivity() {

    private var keepSplashScreenVisible = true

    private val authRepository: AuthRepository by lazy {
        (application as SiPinjamApplication).appContainer.authRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
<<<<<<< HEAD
        var isReady = false
=======
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreenVisible
        }

        super.onCreate(savedInstanceState)
>>>>>>> 909d0f4 (fix: use branded system splash screen)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }

        super.onCreate(savedInstanceState)
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

<<<<<<< HEAD
                            isNavReady = true
=======
>>>>>>> 909d0f4 (fix: use branded system splash screen)
                            isReady = true
                            keepSplashScreenVisible = false
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
}