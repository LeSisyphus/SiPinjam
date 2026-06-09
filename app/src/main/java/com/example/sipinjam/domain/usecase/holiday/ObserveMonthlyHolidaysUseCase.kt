package com.example.sipinjam.domain.usecase.holiday

import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.Flow

class ObserveMonthlyHolidaysUseCase(
    private val holidayRepository: HolidayRepository,
) {
    operator fun invoke(year: Int, month: Int): Flow<List<Holiday>> {
        return holidayRepository.observeMonthlyHolidays(year, month)
    }
}
