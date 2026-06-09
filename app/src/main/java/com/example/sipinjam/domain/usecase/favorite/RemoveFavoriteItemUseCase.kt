package com.example.sipinjam.domain.usecase.favorite

import com.example.sipinjam.domain.repository.FavoriteItemRepository

class RemoveFavoriteItemUseCase(
    private val repository: FavoriteItemRepository,
) {
    suspend operator fun invoke(barangId: String) {
        repository.removeFavorite(barangId)
    }
}
