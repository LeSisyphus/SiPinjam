package com.example.sipinjam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.navigation.NavGraph
import com.cloudinary.android.MediaManager
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init Cloudinary
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key"    to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
        )
        MediaManager.init(this, config)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                var isLoggedIn by remember { mutableStateOf(false) }
                var isAdmin    by remember { mutableStateOf(false) }
                var isReady    by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (authRepository.isLoggedIn()) {
                        val user = authRepository.getCurrentUser()
                        isLoggedIn = true
                        isAdmin    = user?.role == "admin"
                    }
                    isReady = true
                }

                if (isReady) {
                    NavGraph(
                        navController  = navController,
                        isLoggedIn     = isLoggedIn,
                        isAdmin        = isAdmin,
                    )
                }
            }
        }
    }
}