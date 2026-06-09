package com.example.sipinjam.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "holidays",
    primaryKeys = ["date", "name"]
)
data class HolidayEntity(
    val date: String,
    val name: String,
    val isNationalHoliday: Boolean,
    val cachedAt: Long = System.currentTimeMillis(),
)
