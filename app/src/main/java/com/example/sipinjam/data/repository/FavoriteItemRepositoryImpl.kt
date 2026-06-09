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

    override fun observeFavorites(): Flow<List<FavoriteItem>> {
        return favoriteItemDao.observeFavorites()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeIsFavorite(barangId: String): Flow<Boolean> {
        return favoriteItemDao.observeIsFavorite(barangId)
    }

    override suspend fun addFavorite(item: FavoriteItem) {
        favoriteItemDao.upsertFavorite(item.toEntity())
    }

    override suspend fun removeFavorite(barangId: String) {
        favoriteItemDao.deleteFavoriteById(barangId)
    }

    override suspend fun toggleFavorite(item: FavoriteItem) {
        val isFavorite = favoriteItemDao.observeIsFavorite(item.barangId).first()
        if (isFavorite) {
            favoriteItemDao.deleteFavoriteById(item.barangId)
        } else {
            favoriteItemDao.upsertFavorite(item.toEntity())
        }
    }
}
