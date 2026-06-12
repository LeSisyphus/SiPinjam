package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.favorite.GetFavoriteItemsUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase

class FavoritBarangViewModelFactory(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoritBarangViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            getFavoriteItemsUseCase = getFavoriteItemsUseCase,
            toggleFavoriteItemUseCase = toggleFavoriteItemUseCase,
        ) as T
    }
}
