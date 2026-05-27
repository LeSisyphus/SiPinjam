package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DashboardAdminUiState(
    val jumlahPermintaanMasuk: Int = 0,
    val jumlahTersedia: Int = 0,
    val jumlahDipinjam: Int = 0,
    val permintaanTerbaru: List<PermintaanItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardAdminViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(DashboardAdminUiState())
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()

    init {
        hitungStatistikBarang()
    }

    fun hitungStatistikBarang() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("items").get().await()

                var tersediaCount = 0
                var dipinjamCount = 0

                for (document in snapshot.documents) {
                    val tersedia = document.getBoolean("tersedia") ?: true
                    if (tersedia) {
                        tersediaCount++
                    } else {
                        dipinjamCount++
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        jumlahTersedia = tersediaCount,
                        jumlahDipinjam = dipinjamCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Gagal mengambil statistik barang"
                    )
                }
            }
        }
    }

    fun onTinjau(item: PermintaanItem) {
    }
}