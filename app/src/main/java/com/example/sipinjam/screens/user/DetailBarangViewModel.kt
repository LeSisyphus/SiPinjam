package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import com.example.sipinjam.domain.usecase.favorite.ObserveIsFavoriteItemUseCase
import com.example.sipinjam.domain.usecase.favorite.ToggleFavoriteItemUseCase
import com.example.sipinjam.domain.model.FavoriteItem
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
    private val observeIsFavoriteItemUseCase: ObserveIsFavoriteItemUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModel() {

    private val repository = BarangRepositoryImpl()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadBarangDetail(barangId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val barangDoc = repository.getBarangById(barangId)
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

                observeIsFavoriteItemUseCase(barangId).collect { isFav ->
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
        viewModelScope.launch {
            toggleFavoriteItemUseCase(
                FavoriteItem(
                    barangId = barang.id,
                    nama = barang.nama,
                    kategori = barang.kategori,
                    fotoUrl = barang.imageUrl,
                )
            )
        }
    }
}

class DetailBarangViewModelFactory(
    private val observeIsFavoriteItemUseCase: ObserveIsFavoriteItemUseCase,
    private val toggleFavoriteItemUseCase: ToggleFavoriteItemUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailBarangViewModel(
            observeIsFavoriteItemUseCase = observeIsFavoriteItemUseCase,
            toggleFavoriteItemUseCase = toggleFavoriteItemUseCase,
        ) as T
    }
}