package com.example.sipinjam.screens.admin

import com.example.sipinjam.domain.model.BorrowingStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    private var barangListener: ListenerRegistration? = null
    private var peminjamanListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(DashboardAdminUiState(isLoading = true))
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
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Gagal memuat data barang"
                        )
                    }
                    return@addSnapshotListener
                }

                val documents = snapshot?.documents.orEmpty()

                val tersediaCount = documents.count { document ->
                    val stok = document.getLong("stok")?.toInt() ?: 0
                    val tersedia = document.getBoolean("tersedia") ?: (stok > 0)

                    tersedia && stok > 0
                }

                _uiState.update {
                    it.copy(
                        jumlahTersedia = tersediaCount,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
    }

    private fun listenPeminjaman() {
        peminjamanListener?.remove()

        peminjamanListener = firestore.collection("borrowings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Gagal memuat data peminjaman"
                        )
                    }
                    return@addSnapshotListener
                }

                val semuaPeminjaman = snapshot?.documents.orEmpty()

                val permintaanMasuk = semuaPeminjaman.filter { document ->
                    document.getString("status")
                        .equals(BorrowingStatus.DIPROSES, ignoreCase = true)
                }

                val dipinjamCount = semuaPeminjaman.count { document ->
                    val status = document.getString("status").orEmpty()

                    BorrowingStatus.isBorrowed(status)
                }

                viewModelScope.launch {
                    val permintaanTerbaru = permintaanMasuk
                        .sortedByDescending { document ->
                            getCreatedAtMillis(document)
                        }
                        .take(5)
                        .map { document ->
                            document.toPermintaanItem()
                        }

                    _uiState.update {
                        it.copy(
                            jumlahPermintaanMasuk = permintaanMasuk.size,
                            jumlahDipinjam = dipinjamCount,
                            permintaanTerbaru = permintaanTerbaru,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }
    }

    private suspend fun DocumentSnapshot.toPermintaanItem(): PermintaanItem {
        val userId = getString("userId").orEmpty()
        val barangId = getString("barangId").orEmpty()

        val userDocument = getUserDocument(userId)
        val barangDocument = getBarangDocument(barangId)

        val namaUser = userDocument?.getString("nama")
            ?.takeIf { it.isNotBlank() }
            ?: getString("namaUser")
                ?.takeIf { it.isNotBlank() }
            ?: "-"

        val fotoUserUrl = userDocument?.getString("fotoUrl")
            ?.takeIf { it.isNotBlank() }
            ?: ""

        val namaBarang = barangDocument?.getString("nama")
            ?.takeIf { it.isNotBlank() }
            ?: getString("namaBarang")
                ?.takeIf { it.isNotBlank() }
            ?: "-"

        return PermintaanItem(
            id = id,
            nama = namaUser,
            namaBarang = namaBarang,
            waktu = getString("tanggalPinjam") ?: "-",
            fotoUserUrl = fotoUserUrl
        )
    }

    private suspend fun getUserDocument(userId: String): DocumentSnapshot? {
        if (userId.isBlank()) return null

        return try {
            firestore.collection("users")
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBarangDocument(barangId: String): DocumentSnapshot? {
        if (barangId.isBlank()) return null

        return try {
            firestore.collection("items")
                .document(barangId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    private fun getCreatedAtMillis(document: DocumentSnapshot): Long {
        val createdAtLong = document.getLong("createdAt")
        if (createdAtLong != null) return createdAtLong

        val createdAtTimestamp = document.getTimestamp("createdAt")
        if (createdAtTimestamp != null) return createdAtTimestamp.toDate().time

        return 0L
    }

    fun onTinjau(item: PermintaanItem) {
        // Navigasi tetap ditangani oleh DashboardAdminScreen lewat callback onTinjau(item).
        // item.id sudah berisi document id peminjaman untuk membuka detail pengajuan.
    }

    override fun onCleared() {
        super.onCleared()
        barangListener?.remove()
        peminjamanListener?.remove()
    }
}