package com.example.sipinjam.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.sipinjam.data.local.entity.HolidayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays WHERE date LIKE :yearMonthPrefix || '%' ORDER BY date ASC")
    fun observeByMonth(yearMonthPrefix: String): Flow<List<HolidayEntity>>

    @Query("SELECT * FROM holidays ORDER BY date ASC")
    fun observeAll(): Flow<List<HolidayEntity>>

    @Upsert
    suspend fun upsertAll(holidays: List<HolidayEntity>)

    @Query("DELETE FROM holidays WHERE date LIKE :yearMonthPrefix || '%'")
    suspend fun deleteByMonth(yearMonthPrefix: String)
}
