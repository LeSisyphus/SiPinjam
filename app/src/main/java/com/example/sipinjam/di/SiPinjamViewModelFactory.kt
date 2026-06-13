package com.example.sipinjam.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipinjam.screens.admin.DashboardAdminViewModel
import com.example.sipinjam.screens.admin.DetailPengajuanViewModel
import com.example.sipinjam.screens.admin.KelolaBarangViewModel
import com.example.sipinjam.screens.admin.PersetujuanPeminjamanViewModel
import com.example.sipinjam.screens.admin.VerifikasiPengembalianViewModel
import com.example.sipinjam.screens.auth.LoginViewModel
import com.example.sipinjam.screens.auth.RegisterViewModel
import com.example.sipinjam.screens.user.BerandaUserViewModel
import com.example.sipinjam.screens.user.DetailBarangViewModel
import com.example.sipinjam.screens.user.FavoritBarangViewModel
import com.example.sipinjam.screens.user.GantiPasswordViewModel
import com.example.sipinjam.screens.user.KatalogViewModel
import com.example.sipinjam.screens.user.PeminjamanViewModel
import com.example.sipinjam.screens.user.PengembalianViewModel
import com.example.sipinjam.screens.user.ProfilViewModel
import com.example.sipinjam.screens.user.RiwayatPeminjamanViewModel

class SiPinjamViewModelFactory(
    private val appContainer: AppContainer,
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                application = application,
                loginUseCase = appContainer.loginUseCase,
            )

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(
                registerUseCase = appContainer.registerUseCase,
            )

            modelClass.isAssignableFrom(DashboardAdminViewModel::class.java) -> DashboardAdminViewModel(
                observeBarangListUseCase = appContainer.observeBarangListUseCase,
                observePermintaanPeminjamanUseCase = appContainer.observePermintaanPeminjamanUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                getUserByIdUseCase = appContainer.getUserByIdUseCase,
            )

            modelClass.isAssignableFrom(DetailPengajuanViewModel::class.java) -> DetailPengajuanViewModel(
                getPeminjamanDetailUseCase = appContainer.getPeminjamanDetailUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                getUserByIdUseCase = appContainer.getUserByIdUseCase,
                approvePeminjamanUseCase = appContainer.approvePeminjamanUseCase,
                rejectPeminjamanUseCase = appContainer.rejectPeminjamanUseCase,
            )

            modelClass.isAssignableFrom(PersetujuanPeminjamanViewModel::class.java) -> PersetujuanPeminjamanViewModel(
                observePermintaanPeminjamanUseCase = appContainer.observePermintaanPeminjamanUseCase,
                observePengembalianUseCase = appContainer.observePengembalianUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                getUserByIdUseCase = appContainer.getUserByIdUseCase,
            )

            modelClass.isAssignableFrom(VerifikasiPengembalianViewModel::class.java) -> VerifikasiPengembalianViewModel(
                getPengembalianDetailUseCase = appContainer.getPengembalianDetailUseCase,
                getPeminjamanDetailUseCase = appContainer.getPeminjamanDetailUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                getUserByIdUseCase = appContainer.getUserByIdUseCase,
                verifikasiPengembalianUseCase = appContainer.verifikasiPengembalianUseCase,
                tolakPengembalianUseCase = appContainer.tolakPengembalianUseCase,
            )

            modelClass.isAssignableFrom(KelolaBarangViewModel::class.java) -> KelolaBarangViewModel(
                observeBarangListUseCase = appContainer.observeBarangListUseCase,
                addBarangUseCase = appContainer.addBarangUseCase,
                updateBarangUseCase = appContainer.updateBarangUseCase,
                deleteBarangUseCase = appContainer.deleteBarangUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                uploadItemPhotoUseCase = appContainer.uploadItemPhotoUseCase,
            )


            modelClass.isAssignableFrom(BerandaUserViewModel::class.java) -> BerandaUserViewModel(
                getTodayHolidayUseCase = appContainer.getTodayHolidayUseCase,
                observeMonthlyHolidaysUseCase = appContainer.observeMonthlyHolidaysUseCase,
                refreshMonthlyHolidaysUseCase = appContainer.refreshMonthlyHolidaysUseCase,
                observeBarangListUseCase = appContainer.observeBarangListUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                observeRiwayatPeminjamanUseCase = appContainer.observeRiwayatPeminjamanUseCase,
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
            )

            modelClass.isAssignableFrom(DetailBarangViewModel::class.java) -> DetailBarangViewModel(
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                observeIsFavoriteItemUseCase = appContainer.observeIsFavoriteItemUseCase,
                toggleFavoriteItemUseCase = appContainer.toggleFavoriteItemUseCase,
            )

            modelClass.isAssignableFrom(FavoritBarangViewModel::class.java) -> FavoritBarangViewModel(
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                getFavoriteItemsUseCase = appContainer.getFavoriteItemsUseCase,
                toggleFavoriteItemUseCase = appContainer.toggleFavoriteItemUseCase,
            )

            modelClass.isAssignableFrom(KatalogViewModel::class.java) -> KatalogViewModel(
                observeBarangListUseCase = appContainer.observeBarangListUseCase,
                searchBarangUseCase = appContainer.searchBarangUseCase,
            )

            modelClass.isAssignableFrom(PeminjamanViewModel::class.java) -> PeminjamanViewModel(
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                ajukanPeminjamanUseCase = appContainer.ajukanPeminjamanUseCase,
            )

            modelClass.isAssignableFrom(PengembalianViewModel::class.java) -> PengembalianViewModel(
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
                getPengembalianByPeminjamanIdUseCase = appContainer.getPengembalianByPeminjamanIdUseCase,
                ajukanPengembalianUseCase = appContainer.ajukanPengembalianUseCase,
                uploadReturnPhotoUseCase = appContainer.uploadReturnPhotoUseCase,
            )

            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> ProfilViewModel(
                application = application,
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                updateProfileUseCase = appContainer.updateProfileUseCase,
                updateProfilePhotoUrlUseCase = appContainer.updateProfilePhotoUrlUseCase,
                uploadProfilePhotoUseCase = appContainer.uploadProfilePhotoUseCase,
                logoutUseCase = appContainer.logoutUseCase,
            )

            modelClass.isAssignableFrom(GantiPasswordViewModel::class.java) -> GantiPasswordViewModel(
                changePasswordUseCase = appContainer.changePasswordUseCase,
            )

            modelClass.isAssignableFrom(RiwayatPeminjamanViewModel::class.java) -> RiwayatPeminjamanViewModel(
                getCurrentUserUseCase = appContainer.getCurrentUserUseCase,
                observeRiwayatPeminjamanUseCase = appContainer.observeRiwayatPeminjamanUseCase,
                getBarangDetailUseCase = appContainer.getBarangDetailUseCase,
            )

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
