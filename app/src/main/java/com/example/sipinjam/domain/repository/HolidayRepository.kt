package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.model.HolidayStatus
import kotlinx.coroutines.flow.Flow

interface HolidayRepository {
    fun observeMonthlyHolidays(year: Int, month: Int): Flow<List<Holiday>>
    suspend fun refreshMonthlyHolidays(year: Int, month: Int): Result<Unit>
    suspend fun getTodayStatus(): Result<HolidayStatus>
}
