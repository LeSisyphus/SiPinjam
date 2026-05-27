package com.example.sipinjam.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sipinjam.screens.auth.ForgotPasswordScreen
import com.example.sipinjam.screens.auth.RegisterScreen
import com.example.sipinjam.screens.user.DetailBarangScreen
import com.example.sipinjam.screens.admin.DashboardAdminScreen
import com.example.sipinjam.screens.admin.KelolaBarangScreen
import com.example.sipinjam.screens.admin.PersetujuanPeminjamanScreen
import com.example.sipinjam.screens.auth.LoginScreen
import com.example.sipinjam.screens.user.PeminjamanScreen
import com.example.sipinjam.screens.user.BerandaUserScreen
import com.example.sipinjam.screens.user.GantiPasswordScreen
import com.example.sipinjam.screens.user.ProfilScreen
import com.example.sipinjam.screens.user.RiwayatPeminjamanScreen
import com.example.sipinjam.screens.user.PengembalianScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

object Routes {
    const val LOGIN                   = "login"
    const val REGISTER                = "register"
    const val BERANDA_USER            = "beranda_user"
    const val FORGOT_PASSWORD         = "forgot_password"
    const val DETAIL_BARANG = "detail_barang/{barangId}"
    const val AJUKAN_PEMINJAMAN       = "ajukan_peminjaman/{barangId}/{namaBarang}/{kategoriBarang}/{statusBarang}"
    const val DASHBOARD_ADMIN         = "dashboard_admin"
    const val KELOLA_BARANG           = "kelola_barang"
    const val PROFIL                  = "profil"
    const val GANTI_PASSWORD          = "ganti_password"
    const val RIWAYAT_PEMINJAMAN      = "riwayat_peminjaman"
    const val PENGEMBALIAN            = "pengembalian/{namaBarang}/{tanggalPinjam}/{tanggalJatuhTempo}"
    const val PERSETUJUAN_PEMINJAMAN  = "persetujuan_peminjaman"

    fun ajukanPeminjaman(
        barangId: String,
        namaBarang: String,
        kategoriBarang: String,
        statusBarang: String
    ) = "ajukan_peminjaman/$barangId/$namaBarang/$kategoriBarang/$statusBarang"

    fun pengembalian(
        namaBarang: String,
        tanggalPinjam: String,
        tanggalJatuhTempo: String
    ) = "pengembalian/$namaBarang/$tanggalPinjam/$tanggalJatuhTempo"

    fun detailBarang(
        barangId: String
    ) = "detail_barang/$barangId"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean = false,
    startDestination: String = Routes.LOGIN,
    isAdmin: Boolean = false,
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
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() }
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

        composable(Routes.BERANDA_USER) {
            BerandaUserScreen(
                onLihatSemuaBarang = {},
                onBarangClick = { barang -> navController.navigate(Routes.detailBarang(barang.id)) },
                onBerandaClick = {},
                onKatalogClick = {},
                onRiwayatClick = { navController.navigate(Routes.RIWAYAT_PEMINJAMAN) },
                onProfilClick  = { navController.navigate(Routes.PROFIL) }
            )
        }

        composable(
            route = Routes.DETAIL_BARANG,
            arguments = listOf(
                navArgument("barangId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barangId = backStackEntry.arguments?.getString("barangId") ?: ""

            DetailBarangScreen(
                barangId = barangId,
                onBackClick = { navController.popBackStack() },
                onAjukanPeminjaman = { id, nama, kategori, status ->
                    navController.navigate(
                        Routes.ajukanPeminjaman(id, nama, kategori, status)
                    )
                }
            )
        }

        composable(
            route = Routes.AJUKAN_PEMINJAMAN,
            arguments = listOf(
                navArgument("barangId")       { type = NavType.StringType },
                navArgument("namaBarang")     { type = NavType.StringType },
                navArgument("kategoriBarang") { type = NavType.StringType },
                navArgument("statusBarang")   { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val barangId       = backStackEntry.arguments?.getString("barangId") ?: ""
            val namaBarang     = backStackEntry.arguments?.getString("namaBarang") ?: ""
            val kategoriBarang = backStackEntry.arguments?.getString("kategoriBarang") ?: ""
            val statusBarang   = backStackEntry.arguments?.getString("statusBarang") ?: ""

            PeminjamanScreen(
                barangId       = barangId,
                namaBarang     = namaBarang,
                kategoriBarang = kategoriBarang,
                statusBarang   = statusBarang,
                onBackClick    = { navController.popBackStack() },
                onKirimPermohonan = { _, _, _ ->
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.BERANDA_USER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RIWAYAT_PEMINJAMAN) {
            RiwayatPeminjamanScreen(
                onBackClick    = { navController.popBackStack() },
                onBerandaClick = { navController.navigate(Routes.BERANDA_USER) },
                onKatalogClick = {},
                onRiwayatClick = {},
                onProfilClick  = { navController.navigate(Routes.PROFIL) },
                onPengembalianClick = { namaBarang, tanggalPinjam, tanggalJatuhTempo ->
                    navController.navigate(
                        Routes.pengembalian(namaBarang, tanggalPinjam, tanggalJatuhTempo)
                    )
                }
            )
        }

        composable(
            route = Routes.PENGEMBALIAN,
            arguments = listOf(
                navArgument("namaBarang")        { type = NavType.StringType },
                navArgument("tanggalPinjam")     { type = NavType.StringType },
                navArgument("tanggalJatuhTempo") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val namaBarang        = backStackEntry.arguments?.getString("namaBarang") ?: ""
            val tanggalPinjam     = backStackEntry.arguments?.getString("tanggalPinjam") ?: ""
            val tanggalJatuhTempo = backStackEntry.arguments?.getString("tanggalJatuhTempo") ?: ""

            PengembalianScreen(
                namaBarang        = namaBarang,
                tanggalPinjam     = tanggalPinjam,
                tanggalJatuhTempo = tanggalJatuhTempo,
                onBackClick       = { navController.popBackStack() },
                onKirimPengembalian = { _, _ ->
                    navController.navigate(Routes.RIWAYAT_PEMINJAMAN) {
                        popUpTo(Routes.RIWAYAT_PEMINJAMAN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PERSETUJUAN_PEMINJAMAN) {
            PersetujuanPeminjamanScreen(
                onDashboardClick  = { navController.navigate(Routes.DASHBOARD_ADMIN) { popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true } } },
                onBarangClick     = { navController.navigate(Routes.KELOLA_BARANG) },
                onPermintaanClick = {},
                onProfilClick     = { navController.navigate(Routes.PROFIL) }
            )
        }

        composable(Routes.PROFIL) {
            ProfilScreen(
                isAdmin              = false,
                onGantiPasswordClick = { navController.navigate(Routes.GANTI_PASSWORD) },
                onLogoutDone         = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBerandaClick    = { navController.navigate(Routes.BERANDA_USER) { popUpTo(Routes.BERANDA_USER) { inclusive = true } } },
                onKatalogClick    = {},
                onRiwayatClick    = { navController.navigate(Routes.RIWAYAT_PEMINJAMAN) },
                onDashboardClick  = { navController.navigate(Routes.DASHBOARD_ADMIN) { popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true } } },
                onBarangClick     = { navController.navigate(Routes.KELOLA_BARANG) },
                onPermintaanClick = { navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) },
            )
        }

        composable(Routes.GANTI_PASSWORD) {
            GantiPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD_ADMIN) {
            DashboardAdminScreen(
                onLihatSemua      = { navController.navigate(Routes.KELOLA_BARANG) },
                onTinjau          = {},
                onDashboardClick  = {},
                onBarangClick     = { navController.navigate(Routes.KELOLA_BARANG) },
                onPermintaanClick = { navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) },
                onProfilClick     = { navController.navigate(Routes.PROFIL) }
            )
        }

        composable(Routes.KELOLA_BARANG) {
            val adminViewModel: com.example.sipinjam.screens.admin.KelolaBarangViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val adminUiState by adminViewModel.uiState.collectAsState()

            KelolaBarangScreen(
                daftarBarang = adminUiState.daftarBarang,
                showEditDialog = adminUiState.showEditDialog,
                barangToEdit = adminUiState.barangToEdit,
                showDeleteDialog = adminUiState.showDeleteDialog, // 🔥 Alirkan state dialog hapus
                barangToDelete = adminUiState.barangToDelete,     // 🔥 Alirkan data barang target hapus
                onTambahConfirm = { nama, kategori, stok, kondisi, lokasi, maksPinjam, deskripsi ->
                    adminViewModel.onTambahBarangFirestore(nama, kategori, stok, kondisi, lokasi, maksPinjam, deskripsi)
                },
                onEditClick = { barang -> adminViewModel.onEditRequest(barang) },
                onEditConfirm = { id, nama, kategori, stok ->
                    adminViewModel.onEditBarangFirestore(id, nama, kategori, stok)
                },
                onEditDismiss = { adminViewModel.onEditDismiss() },
                onDeleteClick = { barang ->
                    adminViewModel.onDeleteRequest(barang) // 🔥 Saat icon sampah diklik, munculkan konfirmasi
                },
                onDeleteConfirm = {
                    adminViewModel.onDeleteConfirm() // 🔥 Jalankan fungsi delete Firestore
                },
                onDeleteDismiss = {
                    adminViewModel.onDeleteDismiss()
                },
                onDashboardClick  = { navController.navigate(Routes.DASHBOARD_ADMIN) { popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true } } },
                onBarangClick     = {},
                onPermintaanClick = { navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) },
                onProfilClick     = { navController.navigate(Routes.PROFIL) }
            )
        }
    }
}