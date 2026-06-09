package com.example.sipinjam.data.mapper

import com.example.sipinjam.data.local.entity.HolidayEntity
import com.example.sipinjam.data.remote.holiday.HolidayDto
import com.example.sipinjam.data.remote.holiday.HolidayStatusDto
import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.model.HolidayStatus

fun HolidayDto.toEntity(): HolidayEntity? {
    val safeDate = date?.takeIf { it.isNotBlank() } ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null

    return HolidayEntity(
        date = safeDate,
        name = safeName,
        isNationalHoliday = isNationalHoliday ?: false,
    )
}

fun HolidayEntity.toDomain(): Holiday {
    return Holiday(
        date = date,
        name = name,
        isNationalHoliday = isNationalHoliday,
    )
}

fun HolidayStatusDto.toDomain(): HolidayStatus {
    return HolidayStatus(
        date = date.orEmpty(),
        isHoliday = isHoliday ?: false,
        isNationalHoliday = isNationalHoliday ?: false,
        holidayNames = holidayList.orEmpty(),
    )
}
