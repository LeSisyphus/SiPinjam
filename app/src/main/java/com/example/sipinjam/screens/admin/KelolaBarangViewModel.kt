package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class KelolaBarangUiState(
    val daftarBarang: List<BarangAdmin> = emptyList(),
    val searchQuery: String = "",
    val selectedKategori: String = "Semua",
    val showDeleteDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val barangToDelete: BarangAdmin? = null,
    val barangToEdit: BarangAdmin? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
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

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(KelolaBarangUiState())
    val uiState: StateFlow<KelolaBarangUiState> = _uiState.asStateFlow()

    init {
        muatSemuaBarang()
    }

    fun muatSemuaBarang() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("items").get().await()
                val listBarang = snapshot.documents.map { document ->
                    BarangAdmin(
                        id = document.id,
                        nama = document.getString("nama") ?: "Tanpa Nama",
                        kategori = document.getString("kategori") ?: "UMUM",
                        stok = document.getLong("stok")?.toInt() ?: 0,
                        tersedia = document.getBoolean("tersedia") ?: true
                    )
                }
                _uiState.update { it.copy(daftarBarang = listBarang, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onTambahBarangFirestore(
        nama: String, kategori: String, stok: Int, kondisi: String,
        lokasi: String, maksimalPinjam: String, deskripsi: String
    ) {
        _uiState.update { it.copy(isLoading = true, isSuccess = false, errorMessage = null) }
        viewModelScope.launch {
            try {
                val docRef = firestore.collection("items").document()
                val barangBaru = Barang(
                    id = docRef.id, nama = nama, kategori = kategori, stok = stok,
                    tersedia = stok > 0, kondisi = kondisi, lokasi = lokasi,
                    maksimalPinjam = maksimalPinjam, deskripsi = deskripsi, fotoUrl = ""
                )
                docRef.set(barangBaru).await()

                muatSemuaBarang()
                _uiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onEditRequest(barang: BarangAdmin) {
        _uiState.update { it.copy(showEditDialog = true, barangToEdit = barang) }
    }

    fun onEditDismiss() {
        _uiState.update { it.copy(showEditDialog = false, barangToEdit = null) }
    }

    fun onEditBarangFirestore(id: String, nama: String, kategori: String, stok: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val updateData = mapOf(
                    "nama" to nama,
                    "kategori" to kategori,
                    "stok" to stok,
                    "tersedia" to (stok > 0)
                )
                firestore.collection("items").document(id)
                    .set(updateData, SetOptions.merge())
                    .await()

                muatSemuaBarang() // Refresh data
                _uiState.update { it.copy(showEditDialog = false, barangToEdit = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onDeleteRequest(barang: BarangAdmin) {
        _uiState.update { it.copy(showDeleteDialog = true, barangToDelete = barang) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(showDeleteDialog = false, barangToDelete = null) }
    }

    fun onDeleteConfirm() {
        val barang = _uiState.value.barangToDelete ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                firestore.collection("items").document(barang.id).delete().await()

                muatSemuaBarang()
                _uiState.update { it.copy(showDeleteDialog = false, barangToDelete = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onSearchChange(query: String) { _uiState.update { it.copy(searchQuery = query) } }
    fun onKategoriChange(kategori: String) { _uiState.update { it.copy(selectedKategori = kategori) } }
}