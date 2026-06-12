package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface FavoriteItemRepository {
    fun observeFavorites(userId: String): Flow<List<FavoriteItem>>
    fun observeIsFavorite(userId: String, barangId: String): Flow<Boolean>
    suspend fun addFavorite(item: FavoriteItem)
    suspend fun removeFavorite(userId: String, barangId: String)
    suspend fun toggleFavorite(item: FavoriteItem)
}
