package com.example.sipinjam.data.remote.holiday

import com.google.gson.annotations.SerializedName

data class HolidayDto(
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("is_national_holiday")
    val isNationalHoliday: Boolean? = null,
)

data class HolidayStatusDto(
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("is_holiday")
    val isHoliday: Boolean? = null,
    @SerializedName("is_national_holiday")
    val isNationalHoliday: Boolean? = null,
    @SerializedName("holiday_list")
    val holidayList: List<String>? = null,
)
