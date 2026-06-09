package com.example.sipinjam.screens.user

data class FavoriteBarangUiItem(
    val barangId: String,
    val nama: String,
    val kategori: String,
    val fotoUrl: String = "",
    val addedAt: Long = 0L,
)

data class FavoriteBarangUiState(
    val favorites: List<FavoriteBarangUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emptyMessage: String = "Belum ada barang favorit",
)

sealed interface FavoriteBarangUiEvent {
    data class OpenDetail(val barangId: String) : FavoriteBarangUiEvent
    data class RemoveFavorite(val barangId: String) : FavoriteBarangUiEvent
    data object Refresh : FavoriteBarangUiEvent
}
