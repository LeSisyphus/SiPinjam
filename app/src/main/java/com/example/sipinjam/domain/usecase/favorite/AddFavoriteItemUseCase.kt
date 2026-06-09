package com.example.sipinjam.domain.usecase.favorite

import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.repository.FavoriteItemRepository

class AddFavoriteItemUseCase(
    private val repository: FavoriteItemRepository,
) {
    suspend operator fun invoke(item: FavoriteItem) {
        repository.addFavorite(item)
    }
}
