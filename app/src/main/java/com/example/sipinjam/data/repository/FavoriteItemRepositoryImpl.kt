package com.example.sipinjam.data.repository

import com.example.sipinjam.data.local.dao.FavoriteItemDao
import com.example.sipinjam.data.mapper.toDomain
import com.example.sipinjam.data.mapper.toEntity
import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.repository.FavoriteItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteItemRepositoryImpl(
    private val favoriteItemDao: FavoriteItemDao,
) : FavoriteItemRepository {

    override fun observeFavorites(userId: String): Flow<List<FavoriteItem>> {
        return favoriteItemDao.observeFavorites(userId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeIsFavorite(userId: String, barangId: String): Flow<Boolean> {
        return favoriteItemDao.observeIsFavorite(userId, barangId)
    }

    override suspend fun addFavorite(item: FavoriteItem) {
        favoriteItemDao.upsertFavorite(item.toEntity())
    }

    override suspend fun removeFavorite(userId: String, barangId: String) {
        favoriteItemDao.deleteFavoriteById(userId, barangId)
    }

    override suspend fun toggleFavorite(item: FavoriteItem) {
        val isFavorite = favoriteItemDao.observeIsFavorite(item.userId, item.barangId).first()
        if (isFavorite) {
            favoriteItemDao.deleteFavoriteById(item.userId, item.barangId)
        } else {
            favoriteItemDao.upsertFavorite(item.toEntity())
        }
    }
}
