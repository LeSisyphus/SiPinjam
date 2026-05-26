package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Barang
import com.example.sipinjam.data.repository.BarangRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BerandaUiState(
    val barangTersedia: List<BarangTersedia> = emptyList(),
    val itemDikembalikan: List<ItemDikembalikan> = listOf(
        ItemDikembalikan("HDMI Cable 5m", "Lab Multimedia", "12 Mei"),
        ItemDikembalikan("Tripod Excell", "Storage A", "08 Mei"),
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class BerandaUserViewModel : ViewModel() {

    private val repository = BarangRepository()

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    init {
        fetchBarangRealTime()
    }

    private fun fetchBarangRealTime() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getAllBarangRealTime()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.localizedMessage)
                    }
                }
                .collect { listBarangFirestore ->
                    val barangTersediaMapped = listBarangFirestore
                        .filter { it.stok > 0 }
                        .map { barangDoc ->
                            BarangTersedia(
                                nama = barangDoc.nama,
                                kategori = barangDoc.kategori.uppercase(),
                                imageUrl = barangDoc.fotoUrl
                            )
                        }

                    _uiState.update {
                        it.copy(barangTersedia = barangTersediaMapped, isLoading = false)
                    }
                }
        }
    }

    fun onKembalikan(item: ItemDikembalikan) {
        _uiState.update { state ->
            state.copy(
                itemDikembalikan = state.itemDikembalikan.filter { it.nama != item.nama }
            )
        }
    }
}