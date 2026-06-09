package com.example.sipinjam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.navigation.NavGraph

class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by lazy {
        (application as SiPinjamApplication).appContainer.authRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
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