package com.example.sipinjam.domain.model

data class HolidayStatus(
    val date: String,
    val isHoliday: Boolean,
    val isNationalHoliday: Boolean,
    val holidayNames: List<String>,
) {
    val displayName: String
        get() = holidayNames.joinToString(", ").ifBlank { "Bukan hari libur" }
}
