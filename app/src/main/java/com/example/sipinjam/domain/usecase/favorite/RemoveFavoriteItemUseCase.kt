package com.example.sipinjam.domain.usecase.favorite

import com.example.sipinjam.domain.repository.FavoriteItemRepository

class RemoveFavoriteItemUseCase(
    private val repository: FavoriteItemRepository,
) {
    suspend operator fun invoke(userId: String, barangId: String) {
        repository.removeFavorite(userId, barangId)
    }
}
