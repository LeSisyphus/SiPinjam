package com.example.sipinjam.di

import android.content.Context
import com.example.sipinjam.data.local.AppDatabase
import com.example.sipinjam.data.remote.holiday.HolidayRemoteDataSource
import com.example.sipinjam.data.remote.holiday.HolidayRetrofitClient
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import com.example.sipinjam.domain.repository.BarangRepository
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import com.example.sipinjam.data.repository.HolidayRepositoryImpl
import com.example.sipinjam.data.repository.FavoriteItemRepositoryImpl
import com.example.sipinjam.domain.repository.FavoriteItemRepository
import com.example.sipinjam.domain.usecase.favorite.AddFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.GetFavoriteItemsUseCase
import com.example.sipinjam.domain.usecase.favorite.ObserveIsFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.RemoveFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import com.example.sipinjam.domain.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PeminjamanRepositoryImpl
import com.example.sipinjam.domain.repository.HolidayRepository
import com.example.sipinjam.domain.usecase.holiday.GetTodayHolidayUseCase
import com.example.sipinjam.domain.usecase.holiday.ObserveMonthlyHolidaysUseCase
import com.example.sipinjam.domain.usecase.holiday.RefreshMonthlyHolidaysUseCase

import com.example.sipinjam.domain.repository.PengembalianRepository
import com.example.sipinjam.data.repository.PengembalianRepositoryImpl
import com.example.sipinjam.domain.repository.StorageRepository
import com.example.sipinjam.data.repository.StorageRepositoryImpl
import com.example.sipinjam.domain.usecase.auth.CheckAuthStateUseCase
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.auth.LoginUseCase
import com.example.sipinjam.domain.usecase.auth.LogoutUseCase
import com.example.sipinjam.domain.usecase.auth.RegisterUseCase
import com.example.sipinjam.domain.usecase.barang.AddBarangUseCase
import com.example.sipinjam.domain.usecase.barang.DeleteBarangUseCase
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.barang.ObserveBarangListUseCase
import com.example.sipinjam.domain.usecase.barang.SearchBarangUseCase
import com.example.sipinjam.domain.usecase.barang.UpdateBarangUseCase
import com.example.sipinjam.domain.usecase.peminjaman.AjukanPeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ApprovePeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.GetPeminjamanDetailUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ObservePermintaanPeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ObserveRiwayatPeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.RejectPeminjamanUseCase
import com.example.sipinjam.domain.usecase.pengembalian.AjukanPengembalianUseCase
import com.example.sipinjam.domain.usecase.pengembalian.GetPengembalianDetailUseCase
import com.example.sipinjam.domain.usecase.pengembalian.ObservePengembalianUseCase
import com.example.sipinjam.domain.usecase.pengembalian.TolakPengembalianUseCase
import com.example.sipinjam.domain.usecase.pengembalian.VerifikasiPengembalianUseCase
import com.example.sipinjam.domain.usecase.profile.ChangePasswordUseCase
import com.example.sipinjam.domain.usecase.profile.UpdateProfilePhotoUrlUseCase
import com.example.sipinjam.domain.usecase.profile.UpdateProfileUseCase
import com.example.sipinjam.domain.usecase.storage.UploadProfilePhotoUseCase
import com.example.sipinjam.domain.usecase.storage.UploadReturnPhotoUseCase
import com.example.sipinjam.screens.user.BerandaUserViewModelFactory
import com.example.sipinjam.screens.user.FavoritBarangViewModelFactory

class AppContainer(
    private val context: Context,
) {
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl()
    }

    val barangRepository: BarangRepository by lazy {
        BarangRepositoryImpl()
    }

    val peminjamanRepository: PeminjamanRepository by lazy {
        PeminjamanRepositoryImpl()
    }

    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val pengembalianRepository: PengembalianRepository by lazy {
        PengembalianRepositoryImpl()
    }

    val storageRepository: StorageRepository by lazy {
        StorageRepositoryImpl(context)
    }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    val registerUseCase: RegisterUseCase by lazy { RegisterUseCase(authRepository) }
    val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
    val getCurrentUserUseCase: GetCurrentUserUseCase by lazy { GetCurrentUserUseCase(authRepository) }
    val checkAuthStateUseCase: CheckAuthStateUseCase by lazy { CheckAuthStateUseCase(authRepository) }

    val observeBarangListUseCase: ObserveBarangListUseCase by lazy { ObserveBarangListUseCase(barangRepository) }
    val getBarangDetailUseCase: GetBarangDetailUseCase by lazy { GetBarangDetailUseCase(barangRepository) }
    val addBarangUseCase: AddBarangUseCase by lazy { AddBarangUseCase(barangRepository) }
    val updateBarangUseCase: UpdateBarangUseCase by lazy { UpdateBarangUseCase(barangRepository) }
    val deleteBarangUseCase: DeleteBarangUseCase by lazy { DeleteBarangUseCase(barangRepository) }
    val searchBarangUseCase: SearchBarangUseCase by lazy { SearchBarangUseCase() }

    val ajukanPeminjamanUseCase: AjukanPeminjamanUseCase by lazy { AjukanPeminjamanUseCase(peminjamanRepository) }
    val observeRiwayatPeminjamanUseCase: ObserveRiwayatPeminjamanUseCase by lazy { ObserveRiwayatPeminjamanUseCase(peminjamanRepository) }
    val observePermintaanPeminjamanUseCase: ObservePermintaanPeminjamanUseCase by lazy { ObservePermintaanPeminjamanUseCase(peminjamanRepository) }
    val getPeminjamanDetailUseCase: GetPeminjamanDetailUseCase by lazy { GetPeminjamanDetailUseCase(peminjamanRepository) }
    val approvePeminjamanUseCase: ApprovePeminjamanUseCase by lazy { ApprovePeminjamanUseCase(peminjamanRepository) }
    val rejectPeminjamanUseCase: RejectPeminjamanUseCase by lazy { RejectPeminjamanUseCase(peminjamanRepository) }

    val ajukanPengembalianUseCase: AjukanPengembalianUseCase by lazy { AjukanPengembalianUseCase(pengembalianRepository) }
    val observePengembalianUseCase: ObservePengembalianUseCase by lazy { ObservePengembalianUseCase(pengembalianRepository) }
    val getPengembalianDetailUseCase: GetPengembalianDetailUseCase by lazy { GetPengembalianDetailUseCase(pengembalianRepository) }
    val verifikasiPengembalianUseCase: VerifikasiPengembalianUseCase by lazy { VerifikasiPengembalianUseCase(pengembalianRepository) }
    val tolakPengembalianUseCase: TolakPengembalianUseCase by lazy { TolakPengembalianUseCase(pengembalianRepository) }

    val updateProfileUseCase: UpdateProfileUseCase by lazy { UpdateProfileUseCase(authRepository) }
    val changePasswordUseCase: ChangePasswordUseCase by lazy { ChangePasswordUseCase(authRepository) }
    val updateProfilePhotoUrlUseCase: UpdateProfilePhotoUrlUseCase by lazy { UpdateProfilePhotoUrlUseCase(authRepository) }
    val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase by lazy { UploadProfilePhotoUseCase(storageRepository) }
    val uploadReturnPhotoUseCase: UploadReturnPhotoUseCase by lazy { UploadReturnPhotoUseCase(storageRepository) }

    private val holidayRemoteDataSource: HolidayRemoteDataSource by lazy {
        HolidayRemoteDataSource(HolidayRetrofitClient.apiService)
    }

    val holidayRepository: HolidayRepository by lazy {
        HolidayRepositoryImpl(
            remoteDataSource = holidayRemoteDataSource,
            holidayDao = appDatabase.holidayDao(),
        )
    }

    val getTodayHolidayUseCase: GetTodayHolidayUseCase by lazy {
        GetTodayHolidayUseCase(holidayRepository)
    }

    val observeMonthlyHolidaysUseCase: ObserveMonthlyHolidaysUseCase by lazy {
        ObserveMonthlyHolidaysUseCase(holidayRepository)
    }

    val refreshMonthlyHolidaysUseCase: RefreshMonthlyHolidaysUseCase by lazy {
        RefreshMonthlyHolidaysUseCase(holidayRepository)
    }

    val favoriteItemRepository: FavoriteItemRepository by lazy {
        FavoriteItemRepositoryImpl(appDatabase.favoriteItemDao())
    }

    val getFavoriteItemsUseCase: GetFavoriteItemsUseCase by lazy {
        GetFavoriteItemsUseCase(favoriteItemRepository)
    }

    val observeIsFavoriteItemUseCase: ObserveIsFavoriteItemUseCase by lazy {
        ObserveIsFavoriteItemUseCase(favoriteItemRepository)
    }

    val addFavoriteItemUseCase: AddFavoriteItemUseCase by lazy {
        AddFavoriteItemUseCase(favoriteItemRepository)
    }

    val removeFavoriteItemUseCase: RemoveFavoriteItemUseCase by lazy {
        RemoveFavoriteItemUseCase(favoriteItemRepository)
    }

    val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase by lazy {
        ToggleFavoriteItemUseCase(favoriteItemRepository)
    }

    val berandaUserViewModelFactory: BerandaUserViewModelFactory by lazy {
        BerandaUserViewModelFactory(
            getTodayHolidayUseCase = getTodayHolidayUseCase,
            observeMonthlyHolidaysUseCase = observeMonthlyHolidaysUseCase,
            refreshMonthlyHolidaysUseCase = refreshMonthlyHolidaysUseCase,
            barangRepository = barangRepository,
            peminjamanRepository = peminjamanRepository,
            authRepository = authRepository,
        )
    }

    val favoritBarangViewModelFactory: FavoritBarangViewModelFactory by lazy {
        FavoritBarangViewModelFactory(
            getCurrentUserUseCase = getCurrentUserUseCase,
            getFavoriteItemsUseCase = getFavoriteItemsUseCase,
            toggleFavoriteItemUseCase = toggleFavoriteItemUseCase,
        )
    }
}