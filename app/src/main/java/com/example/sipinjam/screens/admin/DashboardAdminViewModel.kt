package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    private var barangListener: ListenerRegistration? = null
    private var peminjamanListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(DashboardAdminUiState())
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()

    init {
        listenStatistikBarang()
        listenPeminjaman()
    }

    private fun listenStatistikBarang() {
        barangListener?.remove()
        barangListener = firestore.collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error.localizedMessage) }
                    return@addSnapshotListener
                }
                var tersediaCount = 0
                for (document in snapshot?.documents ?: emptyList()) {
                    val tersedia = document.getBoolean("tersedia") ?: true
                    if (tersedia) tersediaCount++
                }
                _uiState.update { it.copy(jumlahTersedia = tersediaCount) }
            }
    }

    private fun listenPeminjaman() {
        peminjamanListener?.remove()
        peminjamanListener = firestore.collection("borrowings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error.localizedMessage) }
                    return@addSnapshotListener
                }

                val semuaPeminjaman = snapshot?.documents ?: emptyList()

                val permintaanMasuk = semuaPeminjaman.filter {
                    it.getString("status") == "Diproses"
                }

                val dipinjamCount = semuaPeminjaman.count {
                    val status = it.getString("status") ?: ""
                    status == "Dipinjam" || status == "Disetujui"
                }

                val permintaanTerbaru = permintaanMasuk
                    .sortedByDescending { it.getLong("createdAt") ?: 0L }
                    .take(5)
                    .map { doc ->
                        PermintaanItem(
                            nama = doc.getString("namaUser") ?: "-",
                            namaBarang = doc.getString("namaBarang") ?: "-",
                            waktu = doc.getString("tanggalPinjam") ?: "-"
                        )
                    }

                _uiState.update {
                    it.copy(
                        jumlahPermintaanMasuk = permintaanMasuk.size,
                        jumlahDipinjam = dipinjamCount,
                        permintaanTerbaru = permintaanTerbaru
                    )
                }
            }
    }

    fun onTinjau(item: PermintaanItem) {
    }

    override fun onCleared() {
        super.onCleared()
        barangListener?.remove()
        peminjamanListener?.remove()
    }
}