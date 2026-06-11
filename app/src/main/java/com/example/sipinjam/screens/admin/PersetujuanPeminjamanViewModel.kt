package com.example.sipinjam.screens.admin

import com.example.sipinjam.domain.model.BorrowingStatus
import com.example.sipinjam.domain.model.ReturnStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.model.Pengembalian
import com.example.sipinjam.domain.model.User
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import com.example.sipinjam.domain.repository.BarangRepository
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import com.example.sipinjam.domain.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PeminjamanRepositoryImpl
import com.example.sipinjam.domain.repository.PengembalianRepository
import com.example.sipinjam.data.repository.PengembalianRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class AdminPeminjamanUiItem(
    val peminjaman: Peminjaman,
    val id: String,
    val namaUser: String,
    val roleUser: String,
    val fotoUserUrl: String,
    val namaBarang: String,
    val fotoBarangUrl: String,
    val tanggalLabel: String
)

data class AdminPengembalianUiItem(
    val pengembalian: Pengembalian,
    val id: String,
    val namaUser: String,
    val roleUser: String,
    val fotoUserUrl: String,
    val namaBarang: String,
    val fotoBarangUrl: String,
    val tanggalLabel: String,
    val status: String
)

class PersetujuanPeminjamanViewModel(
    private val peminjamanRepository: PeminjamanRepository = PeminjamanRepositoryImpl(),
    private val pengembalianRepository: PengembalianRepository = PengembalianRepositoryImpl(),
    private val barangRepository: BarangRepository = BarangRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _daftarPeminjaman = MutableStateFlow<List<AdminPeminjamanUiItem>>(emptyList())
    val daftarPeminjaman: StateFlow<List<AdminPeminjamanUiItem>> = _daftarPeminjaman.asStateFlow()

    private val _daftarPengembalian = MutableStateFlow<List<AdminPengembalianUiItem>>(emptyList())
    val daftarPengembalian: StateFlow<List<AdminPengembalianUiItem>> = _daftarPengembalian.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        muatPeminjaman()
        muatPengembalian()
    }

    private fun muatPeminjaman() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            peminjamanRepository.listenSemuaPeminjaman()
                .catch { error ->
                    _errorMessage.value = error.message ?: "Gagal memuat data peminjaman"
                    _isLoading.value = false
                }
                .collect { list ->
                    val filtered = list
                        .filter { it.status.equals(BorrowingStatus.DIPROSES, ignoreCase = true) }
                        .sortedByDescending { it.createdAt }

                    val uiItems = mutableListOf<AdminPeminjamanUiItem>()
                    for (peminjaman in filtered) {
                        uiItems.add(peminjaman.toAdminPeminjamanUiItem())
                    }

                    _daftarPeminjaman.value = uiItems
                    _isLoading.value = false
                }
        }
    }

    private fun muatPengembalian() {
        viewModelScope.launch {
            pengembalianRepository.listenSemuaPengembalian()
                .catch { error ->
                    _errorMessage.value = error.message ?: "Gagal memuat data pengembalian"
                }
                .collect { list ->
                    val filtered = list
                        .filter { it.status.equals(ReturnStatus.MENUNGGU_VERIFIKASI, ignoreCase = true) }
                        .sortedByDescending { it.createdAt }

                    val uiItems = mutableListOf<AdminPengembalianUiItem>()
                    for (pengembalian in filtered) {
                        uiItems.add(pengembalian.toAdminPengembalianUiItem())
                    }

                    _daftarPengembalian.value = uiItems
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private suspend fun Peminjaman.toAdminPeminjamanUiItem(): AdminPeminjamanUiItem {
        val user = getUserById(userId)
        val barang = getBarangById(barangId)

        return AdminPeminjamanUiItem(
            peminjaman = this,
            id = id,
            namaUser = user?.nama?.takeIf { it.isNotBlank() }
                ?: namaUser.ifBlank { "Pengguna" },
            roleUser = formatRole(user),
            fotoUserUrl = user?.fotoUrl.orEmpty(),
            namaBarang = barang?.nama?.takeIf { it.isNotBlank() }
                ?: namaBarang.ifBlank { "Barang" },
            fotoBarangUrl = barang?.fotoUrl.orEmpty(),
            tanggalLabel = formatTanggalPinjam(tanggalPinjam, tanggalKembali)
        )
    }

    private suspend fun Pengembalian.toAdminPengembalianUiItem(): AdminPengembalianUiItem {
        val user = getUserById(userId)
        val barang = getBarangById(barangId)

        return AdminPengembalianUiItem(
            pengembalian = this,
            id = id,
            namaUser = user?.nama?.takeIf { it.isNotBlank() } ?: "Pengguna",
            roleUser = formatRole(user),
            fotoUserUrl = user?.fotoUrl.orEmpty(),
            namaBarang = barang?.nama?.takeIf { it.isNotBlank() } ?: "Barang",
            fotoBarangUrl = barang?.fotoUrl.orEmpty(),
            tanggalLabel = tanggalKembali.ifBlank { "-" },
            status = status.ifBlank { ReturnStatus.MENUNGGU_VERIFIKASI }
        )
    }

    private suspend fun getUserById(userId: String): User? {
        if (userId.isBlank()) return null
        return try {
            authRepository.getUserById(userId)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBarangById(barangId: String): Barang? {
        if (barangId.isBlank()) return null
        return try {
            barangRepository.getBarangById(barangId)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatRole(user: User?): String {
        val role = user?.peran
            ?.takeIf { it.isNotBlank() }
            ?: user?.role?.takeIf { it.isNotBlank() }
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

    private fun formatTanggalPinjam(tanggalPinjam: String, tanggalKembali: String): String {
        return when {
            tanggalPinjam.isNotBlank() && tanggalKembali.isNotBlank() -> {
                "$tanggalPinjam - $tanggalKembali"
            }
            tanggalPinjam.isNotBlank() -> tanggalPinjam
            tanggalKembali.isNotBlank() -> tanggalKembali
            else -> "-"
        }
    }
}