package com.example.sipinjam.domain.usecase.favorite

import com.example.sipinjam.domain.repository.FavoriteItemRepository
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteItemUseCase(
    private val repository: FavoriteItemRepository,
) {
    operator fun invoke(userId: String, barangId: String): Flow<Boolean> {
        return repository.observeIsFavorite(userId, barangId)
    }
}
