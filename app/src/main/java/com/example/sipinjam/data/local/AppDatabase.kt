package com.example.sipinjam.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipinjam.data.local.dao.FavoriteItemDao
import com.example.sipinjam.data.local.dao.HolidayDao
import com.example.sipinjam.data.local.entity.FavoriteItemEntity
import com.example.sipinjam.data.local.entity.HolidayEntity

@Database(
    entities = [
        HolidayEntity::class,
        FavoriteItemEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holidayDao(): HolidayDao
    abstract fun favoriteItemDao(): FavoriteItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sipinjam.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
