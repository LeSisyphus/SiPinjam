package com.example.sipinjam.domain.usecase.favorite

import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.repository.FavoriteItemRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteItemsUseCase(
    private val repository: FavoriteItemRepository,
) {
    operator fun invoke(): Flow<List<FavoriteItem>> {
        return repository.observeFavorites()
    }
}
