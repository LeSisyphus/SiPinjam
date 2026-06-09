package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface FavoriteItemRepository {
    fun observeFavorites(): Flow<List<FavoriteItem>>
    fun observeIsFavorite(barangId: String): Flow<Boolean>
    suspend fun addFavorite(item: FavoriteItem)
    suspend fun removeFavorite(barangId: String)
    suspend fun toggleFavorite(item: FavoriteItem)
}
