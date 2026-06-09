package com.example.sipinjam.data.remote.holiday

class HolidayRemoteDataSource(
    private val apiService: HolidayApiService,
) {
    suspend fun getMonthlyHolidays(year: Int, month: Int): List<HolidayDto> {
        return apiService.getHolidays(year = year, month = month)
    }

    suspend fun getTodayStatus(): HolidayStatusDto {
        return apiService.getTodayStatus()
    }
}
