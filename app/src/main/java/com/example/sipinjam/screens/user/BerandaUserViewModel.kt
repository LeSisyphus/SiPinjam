package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.screens.user.BarangTersedia
import com.example.sipinjam.screens.user.ItemDikembalikan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    init {
        muatBarangDariFirestore()
    }

    private fun muatBarangDariFirestore() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("items")
                    .whereEqualTo("tersedia", true)
                    .get()
                    .await()

                val listBarang = snapshot.documents.map { document ->
                    BarangTersedia(
                        nama = document.getString("nama") ?: "Tanpa Nama",
                        kategori = (document.getString("kategori") ?: "UMUM").uppercase(),
                        imageUrl = document.id
                    )
                }

                _uiState.update {
                    it.copy(barangTersedia = listBarang, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal mengambil data")
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