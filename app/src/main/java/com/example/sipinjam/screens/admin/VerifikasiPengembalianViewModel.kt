package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.PengembalianRepositoryImpl
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.model.Pengembalian
import com.example.sipinjam.domain.model.ReturnStatus
import com.example.sipinjam.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class VerifikasiPengembalianDetailUiState(
    val namaPeminjam: String = "-",
    val rolePeminjam: String = "MAHASISWA",
    val fotoPeminjamUrl: String = "",
    val namaBarang: String = "-",
    val fotoBarangUrl: String = "",
    val catatanPeminjam: String = ""
)

class VerifikasiPengembalianViewModel : ViewModel() {

    private val repository = PengembalianRepositoryImpl()
    private val db = FirebaseFirestore.getInstance()

    private val _pengembalian = MutableStateFlow<Pengembalian?>(null)
    val pengembalian: StateFlow<Pengembalian?> = _pengembalian.asStateFlow()

    private val _detailUiState = MutableStateFlow(VerifikasiPengembalianDetailUiState())
    val detailUiState: StateFlow<VerifikasiPengembalianDetailUiState> =
        _detailUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sukses = MutableStateFlow(false)
    val sukses: StateFlow<Boolean> = _sukses.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _catatanTolak = MutableStateFlow<String?>(null)
    val catatanTolak: StateFlow<String?> = _catatanTolak.asStateFlow()

    fun muatPengembalian(pengembalianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getPengembalianById(pengembalianId)

            result.onSuccess { data ->
                _pengembalian.value = data

                val peminjaman = getPeminjamanById(data.peminjamanId)

                val resolvedUserId = data.userId.ifBlank {
                    peminjaman?.userId.orEmpty()
                }

                val resolvedBarangId = data.barangId.ifBlank {
                    peminjaman?.barangId.orEmpty()
                }

                val user = getUserById(resolvedUserId)
                val barang = getBarangById(resolvedBarangId)

                _detailUiState.value = VerifikasiPengembalianDetailUiState(
                    namaPeminjam = user?.nama?.takeIf { it.isNotBlank() }
                        ?: peminjaman?.namaUser?.takeIf { it.isNotBlank() }
                        ?: "Pengguna",
                    rolePeminjam = formatRole(user),
                    fotoPeminjamUrl = user?.fotoUrl.orEmpty(),
                    namaBarang = barang?.nama?.takeIf { it.isNotBlank() }
                        ?: peminjaman?.namaBarang?.takeIf { it.isNotBlank() }
                        ?: "Barang",
                    fotoBarangUrl = barang?.fotoUrl.orEmpty(),
                    catatanPeminjam = data.catatan
                )

                _catatanTolak.value = if (data.status.equals(ReturnStatus.DITOLAK, ignoreCase = true)) {
                    data.catatanAdmin
                } else {
                    null
                }
            }

            result.onFailure { error ->
                _errorMessage.value = error.message ?: "Gagal memuat data pengembalian"
            }

            _isLoading.value = false
        }
    }

    fun verifikasi(
        pengembalianId: String,
        catatan: String,
        kondisi: String
    ) {
        viewModelScope.launch {
            if (kondisi.isBlank()) {
                _errorMessage.value = "Kondisi barang wajib dipilih"
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.setujuiPengembalian(
                pengembalianId = pengembalianId,
                catatanAdmin = catatan,
                kondisiBarang = kondisi
            )

            if (result.isSuccess) {
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
                    ?: "Gagal memverifikasi pengembalian"
            }

            _isLoading.value = false
        }
    }

    fun tolak(
        pengembalianId: String,
        catatan: String
    ) {
        viewModelScope.launch {
            if (catatan.isBlank()) {
                _errorMessage.value = "Catatan admin wajib diisi saat menolak pengembalian"
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.tolakPengembalian(
                pengembalianId = pengembalianId,
                catatanAdmin = catatan
            )

            if (result.isSuccess) {
                _catatanTolak.value = catatan
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
                    ?: "Gagal menolak pengembalian"
            }

            _isLoading.value = false
        }
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }

    private suspend fun getUserById(userId: String): User? {
        if (userId.isBlank()) return null

        return try {
            val document = db.collection("users")
                .document(userId)
                .get()
                .await()

            document.toObject(User::class.java)?.copy(uid = document.id)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBarangById(barangId: String): Barang? {
        if (barangId.isBlank()) return null

        return try {
            val document = db.collection("items")
                .document(barangId)
                .get()
                .await()

            document.toObject(Barang::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getPeminjamanById(peminjamanId: String): Peminjaman? {
        if (peminjamanId.isBlank()) return null

        return try {
            val document = db.collection("borrowings")
                .document(peminjamanId)
                .get()
                .await()

            document.toObject(Peminjaman::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatRole(user: User?): String {
        val role = user?.peran
            ?.takeIf { it.isNotBlank() }
            ?: user?.role
                ?.takeIf { it.isNotBlank() }
            ?: "MAHASISWA"

        return when {
            role.contains("dosen", ignoreCase = true) -> "DOSEN"
            role.contains("staf", ignoreCase = true) -> "STAF"
            role.contains("staff", ignoreCase = true) -> "STAF"
            role.contains("admin", ignoreCase = true) -> "ADMIN"
            role.contains("peminjam", ignoreCase = true) -> "MAHASISWA"
            else -> role.uppercase()
        }
    }
}