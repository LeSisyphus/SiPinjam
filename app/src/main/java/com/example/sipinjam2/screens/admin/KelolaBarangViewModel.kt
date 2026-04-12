package com.example.sipinjam2.ui.screens.admin

import androidx.lifecycle.ViewModel
import com.example.sipinjam2.ui.screens.admin.BarangAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.filter
import kotlin.collections.map

data class KelolaBarangUiState(
    val daftarBarang: List<BarangAdmin> = listOf(
        BarangAdmin("1", "Camera DSLR Canon", "ELEKTRONIK", 2, true),
        BarangAdmin("2", "Proyektor Epson", "ELEKTRONIK", 1, true),
        BarangAdmin("3", "Tripod Kamera", "OPTIK", 3, true),
        BarangAdmin("4", "Kabel HDMI 5m", "KABEL", 0, false),
    ),
    val searchQuery: String = "",
    val selectedKategori: String = "Semua",
    val showDeleteDialog: Boolean = false,
    val barangToDelete: BarangAdmin? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredBarang: List<BarangAdmin>
        get() = daftarBarang.filter { barang ->
            val matchKategori = selectedKategori == "Semua" ||
                    barang.kategori.equals(selectedKategori, ignoreCase = true)
            val matchSearch = barang.nama.contains(searchQuery, ignoreCase = true) ||
                    barang.id.contains(searchQuery, ignoreCase = true)
            matchKategori && matchSearch
        }
}

class KelolaBarangViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KelolaBarangUiState())
    val uiState: StateFlow<KelolaBarangUiState> = _uiState.asStateFlow()

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onKategoriChange(kategori: String) {
        _uiState.update { it.copy(selectedKategori = kategori) }
    }

    fun onDeleteRequest(barang: BarangAdmin) {
        _uiState.update { it.copy(showDeleteDialog = true, barangToDelete = barang) }
    }

    fun onDeleteConfirm() {
        _uiState.update { state ->
            state.copy(
                daftarBarang = state.daftarBarang.filter { it.id != state.barangToDelete?.id },
                showDeleteDialog = false,
                barangToDelete = null
            )
        }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(showDeleteDialog = false, barangToDelete = null) }
    }

    fun onTambahBarang(barang: BarangAdmin) {
        _uiState.update { state ->
            state.copy(daftarBarang = state.daftarBarang + barang)
        }
    }

    fun onEditBarang(updated: BarangAdmin) {
        _uiState.update { state ->
            state.copy(
                daftarBarang = state.daftarBarang.map {
                    if (it.id == updated.id) updated else it
                }
            )
        }
    }
}