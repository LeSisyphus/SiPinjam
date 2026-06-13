package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.model.FavoriteItem
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.favorite.ObserveIsFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val barang: DetailBarang? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorit: Boolean = false,
)

class DetailBarangViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val observeIsFavoriteItemUseCase: ObserveIsFavoriteItemUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadBarangDetail(barangId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val userId = getCurrentUserUseCase()?.uid.orEmpty()
            currentUserId = userId.ifBlank { null }

            val barangDoc = getBarangDetailUseCase(barangId)
            if (barangDoc != null) {
                val detailMapped = DetailBarang(
                    id = barangDoc.id,
                    nama = barangDoc.nama,
                    kategori = barangDoc.kategori.uppercase(),
                    totalUnit = barangDoc.stok,
                    tersedia = barangDoc.stok > 0,
                    kondisi = barangDoc.kondisi,
                    jumlahTersedia = barangDoc.stok,
                    lokasi = barangDoc.lokasi,
                    maksimalPinjam = "${barangDoc.maksimalPinjam} Hari",
                    deskripsi = barangDoc.deskripsi,
                    imageUrl = barangDoc.fotoUrl
                )
                _uiState.update { it.copy(barang = detailMapped, isLoading = false) }

                if (userId.isBlank()) {
                    _uiState.update { it.copy(isFavorit = false) }
                    return@launch
                }

                observeIsFavoriteItemUseCase(userId, barangId).collect { isFav ->
                    _uiState.update { it.copy(isFavorit = isFav) }
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Barang tidak ditemukan atau gagal dimuat.")
                }
            }
        }
    }

    fun toggleFavorit() {
        val barang = _uiState.value.barang ?: return
        val userId = currentUserId ?: return

        viewModelScope.launch {
            toggleFavoriteItemUseCase(
                FavoriteItem(
                    userId = userId,
                    barangId = barang.id,
                    nama = barang.nama,
                    kategori = barang.kategori,
                    fotoUrl = barang.imageUrl,
                )
            )
        }
    }
}
