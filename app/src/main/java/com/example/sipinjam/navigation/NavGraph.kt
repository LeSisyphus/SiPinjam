package com.example.sipinjam.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sipinjam.screens.admin.DashboardAdminScreen
import com.example.sipinjam.screens.admin.DetailPengajuanScreen
import com.example.sipinjam.screens.admin.KelolaBarangScreen
import com.example.sipinjam.screens.admin.KelolaBarangViewModel
import com.example.sipinjam.screens.admin.PersetujuanPeminjamanScreen
import com.example.sipinjam.screens.admin.VerifikasiPengembalianScreen
import com.example.sipinjam.screens.auth.ForgotPasswordScreen
import com.example.sipinjam.screens.auth.LoginScreen
import com.example.sipinjam.screens.auth.RegisterScreen
import com.example.sipinjam.screens.user.BerandaUserScreen
import com.example.sipinjam.screens.user.DetailBarangScreen
import com.example.sipinjam.screens.user.GantiPasswordScreen
import com.example.sipinjam.screens.user.KatalogScreen
import com.example.sipinjam.screens.user.PeminjamanScreen
import com.example.sipinjam.screens.user.PengembalianScreen
import com.example.sipinjam.screens.user.ProfilScreen
import com.example.sipinjam.screens.user.RiwayatPeminjamanScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"

    const val BERANDA_USER = "beranda_user"
    const val KATALOG_USER = "katalog_user"
    const val PROFIL = "profil"
    const val GANTI_PASSWORD = "ganti_password"
    const val RIWAYAT_PEMINJAMAN = "riwayat_peminjaman"

    private const val DETAIL_BARANG_BASE = "detail_barang"
    const val DETAIL_BARANG = "$DETAIL_BARANG_BASE/{barangId}"

    const val AJUKAN_PEMINJAMAN =
        "ajukan_peminjaman?barangId={barangId}&namaBarang={namaBarang}&kategoriBarang={kategoriBarang}&statusBarang={statusBarang}"

    const val PENGEMBALIAN =
        "pengembalian?peminjamanId={peminjamanId}&barangId={barangId}&userId={userId}&namaBarang={namaBarang}&tanggalPinjam={tanggalPinjam}&tanggalJatuhTempo={tanggalJatuhTempo}"

    const val DASHBOARD_ADMIN = "dashboard_admin"
    const val KELOLA_BARANG = "kelola_barang"
    const val PERSETUJUAN_PEMINJAMAN = "persetujuan_peminjaman"

    private const val DETAIL_PENGAJUAN_BASE = "detail_pengajuan"
    const val DETAIL_PENGAJUAN = "$DETAIL_PENGAJUAN_BASE/{peminjamanId}"

    private const val VERIFIKASI_PENGEMBALIAN_BASE = "verifikasi_pengembalian"
    const val VERIFIKASI_PENGEMBALIAN = "$VERIFIKASI_PENGEMBALIAN_BASE/{pengembalianId}"

    fun detailBarang(barangId: String): String {
        return "$DETAIL_BARANG_BASE/${Uri.encode(barangId)}"
    }

    fun detailPengajuan(peminjamanId: String): String {
        return "$DETAIL_PENGAJUAN_BASE/${Uri.encode(peminjamanId)}"
    }

    fun ajukanPeminjaman(
        barangId: String,
        namaBarang: String,
        kategoriBarang: String,
        statusBarang: String
    ): String {
        return "ajukan_peminjaman" +
                "?barangId=${Uri.encode(barangId)}" +
                "&namaBarang=${Uri.encode(namaBarang)}" +
                "&kategoriBarang=${Uri.encode(kategoriBarang)}" +
                "&statusBarang=${Uri.encode(statusBarang)}"
    }

    fun pengembalian(
        peminjamanId: String,
        barangId: String,
        userId: String,
        namaBarang: String,
        tanggalPinjam: String,
        tanggalJatuhTempo: String
    ): String {
        return "pengembalian" +
                "?peminjamanId=${Uri.encode(peminjamanId)}" +
                "&barangId=${Uri.encode(barangId)}" +
                "&userId=${Uri.encode(userId)}" +
                "&namaBarang=${Uri.encode(namaBarang)}" +
                "&tanggalPinjam=${Uri.encode(tanggalPinjam)}" +
                "&tanggalJatuhTempo=${Uri.encode(tanggalJatuhTempo)}"
    }

    fun verifikasiPengembalian(pengembalianId: String): String {
        return "$VERIFIKASI_PENGEMBALIAN_BASE/${Uri.encode(pengembalianId)}"
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean = false,
    startDestination: String = Routes.LOGIN,
    isAdmin: Boolean = false,
    onAuthStateChanged: (isLoggedIn: Boolean, isAdmin: Boolean) -> Unit = { _, _ -> },
) {
    var currentIsAdmin by rememberSaveable { mutableStateOf(isAdmin) }

    val resolvedStartDestination = when {
        isLoggedIn && currentIsAdmin -> Routes.DASHBOARD_ADMIN
        isLoggedIn && !currentIsAdmin -> Routes.BERANDA_USER
        else -> startDestination
    }

    NavHost(
        navController = navController,
        startDestination = resolvedStartDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { loggedInAsAdmin ->
                    currentIsAdmin = loggedInAsAdmin
                    onAuthStateChanged(true, loggedInAsAdmin)

                    val destination = if (loggedInAsAdmin) {
                        Routes.DASHBOARD_ADMIN
                    } else {
                        Routes.BERANDA_USER
                    }

                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigateSingleTop(Routes.REGISTER)
                },
                onForgotPasswordClick = {
                    navController.navigateSingleTop(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    currentIsAdmin = false
                    onAuthStateChanged(true, false)

                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.BERANDA_USER) {
            BerandaUserScreen(
                onLihatSemuaBarang = {
                    navController.navigateSingleTop(Routes.KATALOG_USER)
                },
                onBarangClick = { barang ->
                    if (barang.id.isNotBlank()) {
                        navController.navigate(Routes.detailBarang(barang.id))
                    }
                },
                onBerandaClick = {},
                onKatalogClick = {
                    navController.navigateSingleTop(Routes.KATALOG_USER)
                },
                onRiwayatClick = {
                    navController.navigateSingleTop(Routes.RIWAYAT_PEMINJAMAN)
                },
                onProfilClick = {
                    navController.navigateSingleTop(Routes.PROFIL)
                },
                onPengembalianClick = { peminjamanId, barangId, userId, namaBarang, tanggalPinjam, tanggalJatuhTempo ->
                    navController.navigate(
                        Routes.pengembalian(
                            peminjamanId = peminjamanId,
                            barangId = barangId,
                            userId = userId,
                            namaBarang = namaBarang,
                            tanggalPinjam = tanggalPinjam,
                            tanggalJatuhTempo = tanggalJatuhTempo
                        )
                    )
                }
            )
        }

        composable(Routes.KATALOG_USER) {
            KatalogScreen(
                onBarangClick = { barang ->
                    if (barang.id.isNotBlank()) {
                        navController.navigate(Routes.detailBarang(barang.id))
                    }
                },
                onBerandaClick = {
                    navController.navigateSingleTop(Routes.BERANDA_USER)
                },
                onKatalogClick = {},
                onRiwayatClick = {
                    navController.navigateSingleTop(Routes.RIWAYAT_PEMINJAMAN)
                },
                onProfilClick = {
                    navController.navigateSingleTop(Routes.PROFIL)
                }
            )
        }

        composable(
            route = Routes.DETAIL_BARANG,
            arguments = listOf(
                navArgument("barangId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val barangId = backStackEntry.arguments?.getString("barangId").orEmpty()

            DetailBarangScreen(
                barangId = barangId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAjukanPeminjaman = { id, namaBarang, kategoriBarang, statusBarang ->
                    navController.navigate(
                        Routes.ajukanPeminjaman(
                            barangId = id,
                            namaBarang = namaBarang,
                            kategoriBarang = kategoriBarang,
                            statusBarang = statusBarang
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.AJUKAN_PEMINJAMAN,
            arguments = listOf(
                navArgument("barangId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("namaBarang") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("kategoriBarang") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("statusBarang") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            )
        ) { backStackEntry ->
            val barangId = backStackEntry.arguments?.getString("barangId").orEmpty()
            val namaBarang = backStackEntry.arguments?.getString("namaBarang").orEmpty()
            val kategoriBarang = backStackEntry.arguments?.getString("kategoriBarang").orEmpty()
            val statusBarang = backStackEntry.arguments?.getString("statusBarang").orEmpty()

            PeminjamanScreen(
                barangId = barangId,
                namaBarang = namaBarang,
                kategoriBarang = kategoriBarang,
                statusBarang = statusBarang,
                onBackClick = {
                    navController.popBackStack()
                },
                onKirimPermohonan = { _, _, _ ->
                    navController.navigate(Routes.BERANDA_USER) {
                        popUpTo(Routes.BERANDA_USER) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.RIWAYAT_PEMINJAMAN) {
            RiwayatPeminjamanScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBerandaClick = {
                    navController.navigateSingleTop(Routes.BERANDA_USER)
                },
                onKatalogClick = {
                    navController.navigateSingleTop(Routes.KATALOG_USER)
                },
                onRiwayatClick = {},
                onProfilClick = {
                    navController.navigateSingleTop(Routes.PROFIL)
                },
                onPengembalianClick = { peminjamanId, barangId, userId, namaBarang, tanggalPinjam, tanggalJatuhTempo ->
                    navController.navigate(
                        Routes.pengembalian(
                            peminjamanId = peminjamanId,
                            barangId = barangId,
                            userId = userId,
                            namaBarang = namaBarang,
                            tanggalPinjam = tanggalPinjam,
                            tanggalJatuhTempo = tanggalJatuhTempo
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.PENGEMBALIAN,
            arguments = listOf(
                navArgument("peminjamanId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("barangId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("userId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("namaBarang") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("tanggalPinjam") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("tanggalJatuhTempo") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            )
        ) { backStackEntry ->
            val peminjamanId = backStackEntry.arguments?.getString("peminjamanId").orEmpty()
            val barangId = backStackEntry.arguments?.getString("barangId").orEmpty()
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            val namaBarang = backStackEntry.arguments?.getString("namaBarang").orEmpty()
            val tanggalPinjam = backStackEntry.arguments?.getString("tanggalPinjam").orEmpty()
            val tanggalJatuhTempo = backStackEntry.arguments?.getString("tanggalJatuhTempo").orEmpty()

            PengembalianScreen(
                peminjamanId = peminjamanId,
                barangId = barangId,
                userId = userId,
                namaBarang = namaBarang,
                tanggalPinjam = tanggalPinjam,
                tanggalJatuhTempo = tanggalJatuhTempo,
                onBackClick = {
                    navController.popBackStack()
                },
                onKirimPengembalian = {
                    navController.navigate(Routes.RIWAYAT_PEMINJAMAN) {
                        popUpTo(Routes.RIWAYAT_PEMINJAMAN) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.DASHBOARD_ADMIN) {
            DashboardAdminScreen(
                onLihatSemua = {
                    navController.navigateSingleTop(Routes.KELOLA_BARANG)
                },
                onTinjau = {
                    navController.navigateSingleTop(Routes.PERSETUJUAN_PEMINJAMAN)
                },
                onDashboardClick = {},
                onBarangClick = {
                    navController.navigateSingleTop(Routes.KELOLA_BARANG)
                },
                onPermintaanClick = {
                    navController.navigateSingleTop(Routes.PERSETUJUAN_PEMINJAMAN)
                },
                onProfilClick = {
                    navController.navigateSingleTop(Routes.PROFIL)
                }
            )
        }

        composable(Routes.KELOLA_BARANG) {
            val context = LocalContext.current
            val adminViewModel: com.example.sipinjam.screens.admin.KelolaBarangViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val adminUiState by adminViewModel.uiState.collectAsState()

            KelolaBarangScreen(
                daftarBarang = adminUiState.filteredBarang,
                showEditDialog = adminUiState.showEditDialog,
                barangToEdit = adminUiState.barangToEdit,
                showDeleteDialog = adminUiState.showDeleteDialog,
                barangToDelete = adminUiState.barangToDelete,
                isLoading = adminUiState.isLoading,
                isSuccess = adminUiState.isSuccess,
                isEditSuccess = adminUiState.isEditSuccess, // 🔥 Aliran data baru untuk Pop-up Edit
                isDeleteSuccess = adminUiState.isDeleteSuccess,
                onTambahConfirm = { nama, kategori, stok, kondisi, lokasi, maksPinjam, deskripsi, imageUri ->
                    adminViewModel.onTambahBarangCloudinary(context, nama, kategori, stok, kondisi, lokasi, maksPinjam, deskripsi, imageUri)
                },
                onEditClick = { barang -> adminViewModel.onEditRequest(barang) },
                onEditConfirm = { id, nama, kategori, stok, imageUri ->
                    adminViewModel.onEditBarangFirestore(context, id, nama, kategori, stok, imageUri)
                },
                onEditDismiss = { adminViewModel.onEditDismiss() },
                onDeleteClick = { barang -> adminViewModel.onDeleteRequest(barang) },
                onDeleteConfirm = { adminViewModel.onDeleteConfirm() },
                onDeleteDismiss = { adminViewModel.onDeleteDismiss() },
                onSuccessDismiss = { adminViewModel.resetSuccessState() },
                onDashboardClick  = { navController.navigate(Routes.DASHBOARD_ADMIN) { popUpTo(Routes.DASHBOARD_ADMIN) { inclusive = true } } },
                onBarangClick     = {},
                onPermintaanClick = { navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) },
                onProfilClick     = { navController.navigate(Routes.PROFIL) }
            )
        }
        composable(Routes.PERSETUJUAN_PEMINJAMAN) {
            PersetujuanPeminjamanScreen(
                onDashboardClick = {
                    navController.navigateSingleTop(Routes.DASHBOARD_ADMIN)
                },
                onBarangClick = {
                    navController.navigateSingleTop(Routes.KELOLA_BARANG)
                },
                onPermintaanClick = {},
                onProfilClick = {
                    navController.navigateSingleTop(Routes.PROFIL)
                },
                onDetailPengajuanClick = { peminjamanId ->
                    navController.navigate(
                        Routes.detailPengajuan(peminjamanId)
                    )
                },
                onVerifikasiClick = { pengembalianId ->
                    navController.navigate(
                        Routes.verifikasiPengembalian(pengembalianId)
                    )
                }
            )
        }

        composable(
            route = Routes.DETAIL_PENGAJUAN,
            arguments = listOf(
                navArgument("peminjamanId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val peminjamanId = backStackEntry.arguments?.getString("peminjamanId").orEmpty()

            DetailPengajuanScreen(
                peminjamanId = peminjamanId,
                onBackClick = {
                    navController.popBackStack()
                },
                onActionDone = {
                    navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) {
                        popUpTo(Routes.PERSETUJUAN_PEMINJAMAN) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.VERIFIKASI_PENGEMBALIAN,
            arguments = listOf(
                navArgument("pengembalianId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val pengembalianId = backStackEntry.arguments?.getString("pengembalianId").orEmpty()

            VerifikasiPengembalianScreen(
                pengembalianId = pengembalianId,
                onBackClick = {
                    navController.popBackStack()
                },
                onVerifikasiDone = {
                    navController.navigate(Routes.PERSETUJUAN_PEMINJAMAN) {
                        popUpTo(Routes.PERSETUJUAN_PEMINJAMAN) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.PROFIL) {
            ProfilScreen(
                isAdmin = currentIsAdmin,
                onGantiPasswordClick = {
                    navController.navigateSingleTop(Routes.GANTI_PASSWORD)
                },
                onLogoutDone = {
                    currentIsAdmin = false
                    onAuthStateChanged(false, false)

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBerandaClick = {
                    navController.navigateSingleTop(Routes.BERANDA_USER)
                },
                onKatalogClick = {
                    navController.navigateSingleTop(Routes.KATALOG_USER)
                },
                onRiwayatClick = {
                    navController.navigateSingleTop(Routes.RIWAYAT_PEMINJAMAN)
                },
                onDashboardClick = {
                    navController.navigateSingleTop(Routes.DASHBOARD_ADMIN)
                },
                onBarangClick = {
                    navController.navigateSingleTop(Routes.KELOLA_BARANG)
                },
                onPermintaanClick = {
                    navController.navigateSingleTop(Routes.PERSETUJUAN_PEMINJAMAN)
                },
            )
        }

        composable(Routes.GANTI_PASSWORD) {
            GantiPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}