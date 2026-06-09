package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.domain.usecase.holiday.GetTodayHolidayUseCase
import com.example.sipinjam.domain.usecase.holiday.ObserveMonthlyHolidaysUseCase
import com.example.sipinjam.domain.usecase.holiday.RefreshMonthlyHolidaysUseCase

class BerandaUserViewModelFactory(
    private val getTodayHolidayUseCase: GetTodayHolidayUseCase,
    private val observeMonthlyHolidaysUseCase: ObserveMonthlyHolidaysUseCase,
    private val refreshMonthlyHolidaysUseCase: RefreshMonthlyHolidaysUseCase,
    private val barangRepository: BarangRepository,
    private val peminjamanRepository: PeminjamanRepository,
    private val authRepository: AuthRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BerandaUserViewModel::class.java)) {
            return BerandaUserViewModel(
                getTodayHolidayUseCase = getTodayHolidayUseCase,
                observeMonthlyHolidaysUseCase = observeMonthlyHolidaysUseCase,
                refreshMonthlyHolidaysUseCase = refreshMonthlyHolidaysUseCase,
                barangRepository = barangRepository,
                peminjamanRepository = peminjamanRepository,
                authRepository = authRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
