package com.example.sipinjam.data.repository

import com.example.sipinjam.data.local.dao.HolidayDao
import com.example.sipinjam.data.mapper.toDomain
import com.example.sipinjam.data.mapper.toEntity
import com.example.sipinjam.data.remote.holiday.HolidayRemoteDataSource
import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.model.HolidayStatus
import com.example.sipinjam.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HolidayRepositoryImpl(
    private val remoteDataSource: HolidayRemoteDataSource,
    private val holidayDao: HolidayDao,
) : HolidayRepository {

    override fun observeMonthlyHolidays(year: Int, month: Int): Flow<List<Holiday>> {
        return holidayDao.observeByMonth(yearMonthPrefix = yearMonthPrefix(year, month))
            .map { cachedHolidays -> cachedHolidays.map { it.toDomain() } }
    }

    override suspend fun refreshMonthlyHolidays(year: Int, month: Int): Result<Unit> {
        return try {
            val remoteHolidays = remoteDataSource.getMonthlyHolidays(year = year, month = month)
                .mapNotNull { it.toEntity() }

            val prefix = yearMonthPrefix(year, month)
            holidayDao.deleteByMonth(prefix)
            holidayDao.upsertAll(remoteHolidays)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getTodayStatus(): Result<HolidayStatus> {
        return try {
            Result.success(remoteDataSource.getTodayStatus().toDomain())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun yearMonthPrefix(year: Int, month: Int): String {
        return "%04d-%02d".format(year, month)
    }
}
