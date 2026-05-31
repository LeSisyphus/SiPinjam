package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.BarangRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val barang: DetailBarang? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailBarangViewModel : ViewModel() {

    private val repository = BarangRepository()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadBarangDetail(barangId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getBarangByIdRealTime(barangId).collect { barangDoc ->
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
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Barang tidak ditemukan atau gagal dimuat.")
                    }
                }
            }
        }
    }
}