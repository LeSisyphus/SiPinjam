package com.example.sipinjam2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.ui.screens.user.DetailBarang
import com.example.sipinjam.ui.screens.user.DetailBarangScreen
import com.example.sipinjam2.ui.screens.admin.DashboardAdminScreen
import com.example.sipinjam2.ui.screens.admin.KelolaBarangScreen
import com.example.sipinjam2.ui.screens.auth.LoginScreen
import com.example.sipinjam2.ui.screens.user.PeminjamanScreen
import com.example.sipinjam2.ui.screens.user.BerandaUserScreen

object Routes {
    const val LOGIN             = "login"
    const val BERANDA_USER      = "beranda_user"
    const val DETAIL_BARANG     = "detail_barang"
    const val AJUKAN_PEMINJAMAN = "ajukan_peminjaman"
    const val DASHBOARD_ADMIN   = "dashboard_admin"
    const val KELOLA_BARANG     = "kelola_barang"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN,
) {
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
                onRegisterClick = {},
                onForgotPasswordClick = {}
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
                onProfilClick = {}
            )
        }

        composable(Routes.DETAIL_BARANG) {
            DetailBarangScreen(
                barang = DetailBarang(
                    nama = "MacBook Pro M2 14-inch Space Gray",
                    kategori = "ELEKTRONIK",
                    totalUnit = 12,
                    tersedia = true,
                    kondisi = "Sangat Baik",
                    jumlahTersedia = 8,
                    lokasi = "Lab Komputer",
                    maksimalPinjam = "14 Hari",
                    deskripsi = "Laptop performa tinggi dengan chip M2 Pro. Cocok untuk kebutuhan desain grafis, editing video, dan pengembangan software.",
                ),
                onBackClick = {
                    navController.popBackStack()
                },
                onShareClick = {},
                onAjukanPeminjaman = {
                    navController.navigate(Routes.AJUKAN_PEMINJAMAN)
                }
            )
        }

        composable(Routes.AJUKAN_PEMINJAMAN) {
            PeminjamanScreen(
                namaBarang = "MacBook Pro M2 14-inch",
                kategoriBarang = "ELEKTRONIK",
                statusBarang = "TERSEDIA",
                onBackClick = {
                    navController.popBackStack()
                },
                onKirimPermohonan = { _, _, _ ->
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.BERANDA_USER) { inclusive = true }
                    }
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
                onProfilClick = {}
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
                onProfilClick = {}
            )
        }
    }
}