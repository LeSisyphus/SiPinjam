package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.usecase.favorite.GetFavoriteItemsUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritBarangUiState(
    val daftarFavorit: List<FavoriteItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class FavoritBarangViewModel(
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritBarangUiState())
    val uiState: StateFlow<FavoritBarangUiState> = _uiState.asStateFlow()

    init {
        muatFavorit()
    }

    private fun muatFavorit() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoriteItemsUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal memuat favorit")
                    }
                }
                .collect { list ->
                    _uiState.update {
                        it.copy(daftarFavorit = list, isLoading = false)
                    }
                }
        }
    }

    fun hapusFavorit(item: FavoriteItem) {
        viewModelScope.launch {
            toggleFavoriteItemUseCase(item)
        }
    }
}