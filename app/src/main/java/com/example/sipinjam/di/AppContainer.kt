package com.example.sipinjam.di

import android.content.Context
import com.example.sipinjam.data.local.AppDatabase
import com.example.sipinjam.data.remote.holiday.HolidayRemoteDataSource
import com.example.sipinjam.data.remote.holiday.HolidayRetrofitClient
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.HolidayRepositoryImpl
import com.example.sipinjam.data.repository.FavoriteItemRepositoryImpl
import com.example.sipinjam.domain.repository.FavoriteItemRepository
import com.example.sipinjam.domain.usecase.favorite.AddFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.GetFavoriteItemsUseCase
import com.example.sipinjam.domain.usecase.favorite.ObserveIsFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.RemoveFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.domain.repository.HolidayRepository
import com.example.sipinjam.domain.usecase.holiday.GetTodayHolidayUseCase
import com.example.sipinjam.domain.usecase.holiday.ObserveMonthlyHolidaysUseCase
import com.example.sipinjam.domain.usecase.holiday.RefreshMonthlyHolidaysUseCase
import com.example.sipinjam.screens.user.BerandaUserViewModelFactory
import com.example.sipinjam.screens.user.FavoritBarangViewModelFactory

class AppContainer(
    private val context: Context,
) {
    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    val barangRepository: BarangRepository by lazy {
        BarangRepository()
    }

    val peminjamanRepository: PeminjamanRepository by lazy {
        PeminjamanRepository()
    }

    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

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
            getFavoriteItemsUseCase = getFavoriteItemsUseCase,
            toggleFavoriteItemUseCase = toggleFavoriteItemUseCase,
        )
    }
}