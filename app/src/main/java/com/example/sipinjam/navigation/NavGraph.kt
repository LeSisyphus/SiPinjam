package com.example.sipinjam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.screens.auth.ForgotPasswordScreen
import com.example.sipinjam.screens.auth.RegisterScreen
import com.example.sipinjam.screens.user.DetailBarangScreen
import com.example.sipinjam.screens.admin.DashboardAdminScreen
import com.example.sipinjam.screens.admin.KelolaBarangScreen
import com.example.sipinjam.screens.auth.LoginScreen
import com.example.sipinjam.screens.user.PeminjamanScreen
import com.example.sipinjam.screens.user.BerandaUserScreen
import com.example.sipinjam.screens.user.GantiPasswordScreen
import com.example.sipinjam.screens.user.ProfilScreen

object Routes {
    const val LOGIN             = "login"
    const val REGISTER = "register"
    const val BERANDA_USER      = "beranda_user"
    const val FORGOT_PASSWORD   = "forgot_password"
    const val DETAIL_BARANG     = "detail_barang"
    const val AJUKAN_PEMINJAMAN = "ajukan_peminjaman"
    const val DASHBOARD_ADMIN   = "dashboard_admin"
    const val KELOLA_BARANG     = "kelola_barang"
    const val PROFIL          = "profil"
    const val GANTI_PASSWORD  = "ganti_password"

}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean = false,
    startDestination: String = Routes.LOGIN,
    isAdmin: Boolean = false,
) {
    val start = if (isLoggedIn) Routes.BERANDA_USER else Routes.LOGIN
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Routes.DASHBOARD_ADMIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.BERANDA_USER) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) } // ← isi ini
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFIL) {
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val adminRoutes = listOf(Routes.DASHBOARD_ADMIN, Routes.KELOLA_BARANG)
            val fromAdmin = previousRoute in adminRoutes

            ProfilScreen(
                isAdmin           = fromAdmin,
                onGantiPasswordClick = { navController.navigate(Routes.GANTI_PASSWORD) },
                onLogoutDone      = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBerandaClick    = {
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.BERANDA_USER) { inclusive = true }
                    }
                },
                onKatalogClick    = { navController.navigate(Routes.DETAIL_BARANG) },
                onRiwayatClick    = {},
                onDashboardClick  = {
                    navController.navigate(Routes.DASHBOARD_ADMIN) {
                        popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true }
                    }
                },
                onBarangClick     = { navController.navigate(Routes.KELOLA_BARANG) },
                onPermintaanClick = {},
            )
        }

        composable(Routes.GANTI_PASSWORD) {
            GantiPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }


        composable(Routes.BERANDA_USER) {
            BerandaUserScreen(
                onLihatSemuaBarang = {
                    navController.navigate(Routes.DETAIL_BARANG)
                },
                onBarangClick = {
                    navController.navigate(Routes.DETAIL_BARANG)
                },
                onBerandaClick = {},
                onKatalogClick = {
                    navController.navigate(Routes.DETAIL_BARANG)
                },
                onRiwayatClick = {},
                onProfilClick = { navController.navigate(Routes.PROFIL) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.DETAIL_BARANG) {
            DetailBarangScreen(
                onBackClick = {
                    navController.navigate(Routes.BERANDA_USER)
                },
                onAjukanPeminjaman = {
                    navController.navigate(Routes.AJUKAN_PEMINJAMAN)
                }
            )
        }

        composable(Routes.AJUKAN_PEMINJAMAN) {
            PeminjamanScreen(
                onKirimPermohonan = { _, _, _ ->
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.BERANDA_USER) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigate(Routes.DETAIL_BARANG)
                }
            )
        }

        composable(Routes.DASHBOARD_ADMIN) {
            DashboardAdminScreen(
                onLihatSemua = {
                    navController.navigate(Routes.KELOLA_BARANG)
                },
                onTinjau = {},
                onDashboardClick = {},
                onBarangClick = {
                    navController.navigate(Routes.KELOLA_BARANG)
                },
                onPermintaanClick = {},
                onProfilClick = { navController.navigate(Routes.PROFIL) }
            )
        }

        composable(Routes.KELOLA_BARANG) {
            KelolaBarangScreen(
                onTambahClick = {},
                onEditClick = {},
                onDashboardClick = {
                    navController.navigate(Routes.DASHBOARD_ADMIN) {
                        popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true }
                    }
                },
                onBarangClick = {},
                onPermintaanClick = {},
                onProfilClick = { navController.navigate(Routes.PROFIL) }
            )
        }
    }
}