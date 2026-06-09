package com.example.sipinjam.data.remote.holiday

import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApiService {
    @GET("api")
    suspend fun getHolidays(
        @Query("year") year: Int,
        @Query("month") month: Int? = null,
    ): List<HolidayDto>

    @GET("api/today")
    suspend fun getTodayStatus(): HolidayStatusDto
}
