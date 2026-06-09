package com.example.sipinjam.domain.usecase.holiday

import com.example.sipinjam.domain.repository.HolidayRepository

class RefreshMonthlyHolidaysUseCase(
    private val holidayRepository: HolidayRepository,
) {
    suspend operator fun invoke(year: Int, month: Int): Result<Unit> {
        return holidayRepository.refreshMonthlyHolidays(year, month)
    }
}
