package com.example.sipinjam.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.sipinjam.data.local.entity.FavoriteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteItemDao {
    @Query("SELECT * FROM favorite_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeFavorites(userId: String): Flow<List<FavoriteItemEntity>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM favorite_items
            WHERE userId = :userId AND barangId = :barangId
        )
        """
    )
    fun observeIsFavorite(userId: String, barangId: String): Flow<Boolean>

    @Upsert
    suspend fun upsertFavorite(item: FavoriteItemEntity)

    @Delete
    suspend fun deleteFavorite(item: FavoriteItemEntity)

    @Query("DELETE FROM favorite_items WHERE userId = :userId AND barangId = :barangId")
    suspend fun deleteFavoriteById(userId: String, barangId: String)

    @Query("DELETE FROM favorite_items WHERE userId = :userId")
    suspend fun clearFavoritesByUser(userId: String)
}
