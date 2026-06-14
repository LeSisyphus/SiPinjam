package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.favorite.GetFavoriteItemsUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.sipinjam.utils.UiMessageKey

data class FavoritBarangUiState(
    val daftarFavorit: List<FavoriteItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class FavoritBarangViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritBarangUiState())
    val uiState: StateFlow<FavoritBarangUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        muatFavorit()
    }

    private fun muatFavorit() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()?.uid.orEmpty()
            currentUserId = userId.ifBlank { null }

            if (userId.isBlank()) {
                _uiState.update {
                    it.copy(
                        daftarFavorit = emptyList(),
                        isLoading = false,
                        errorMessage = UiMessageKey.USER_NOT_LOGGED_IN
                    )
                }
                return@launch
            }

            getFavoriteItemsUseCase(userId)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = UiMessageKey.LOAD_FAVORITES_FAILED)
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
        val userId = currentUserId ?: return
        viewModelScope.launch {
            toggleFavoriteItemUseCase(item.copy(userId = userId))
        }
    }
}
