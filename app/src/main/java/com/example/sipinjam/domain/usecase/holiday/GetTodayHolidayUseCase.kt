package com.example.sipinjam.domain.usecase.holiday

import com.example.sipinjam.domain.model.HolidayStatus
import com.example.sipinjam.domain.repository.HolidayRepository

class GetTodayHolidayUseCase(
    private val holidayRepository: HolidayRepository,
) {
    suspend operator fun invoke(): Result<HolidayStatus> {
        return holidayRepository.getTodayStatus()
    }
}
