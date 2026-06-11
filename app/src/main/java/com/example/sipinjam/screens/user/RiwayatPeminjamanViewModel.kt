package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import com.example.sipinjam.domain.repository.BarangRepository
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import com.example.sipinjam.domain.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PeminjamanRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class RiwayatPeminjamanUiItem(
    val peminjaman: Peminjaman,
    val id: String,
    val barangId: String,
    val userId: String,
    val namaBarang: String,
    val fotoBarangUrl: String,
    val tanggalPinjam: String,
    val tanggalKembali: String,
    val status: String
)

class RiwayatPeminjamanViewModel : ViewModel() {

    private val repository = PeminjamanRepositoryImpl()
    private val barangRepository = BarangRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()

    private val _daftarPeminjaman = MutableStateFlow<List<RiwayatPeminjamanUiItem>>(emptyList())
    val daftarPeminjaman: StateFlow<List<RiwayatPeminjamanUiItem>> = _daftarPeminjaman.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var hasStartedListening = false

    fun muatRiwayat() {
        if (hasStartedListening) return
        hasStartedListening = true

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _isLoading.value = false
                _errorMessage.value = "User tidak ditemukan, silakan login ulang"
                return@launch
            }

            repository.listenPeminjamanByUser(currentUser.uid)
                .catch { error ->
                    _isLoading.value = false
                    _errorMessage.value = error.message ?: "Gagal memuat riwayat peminjaman"
                }
                .collect { list ->
                    val uiItems = mutableListOf<RiwayatPeminjamanUiItem>()

                    for (peminjaman in list) {
                        val barang = barangRepository.getBarangById(peminjaman.barangId)

                        uiItems.add(
                            RiwayatPeminjamanUiItem(
                                peminjaman = peminjaman,
                                id = peminjaman.id,
                                barangId = peminjaman.barangId,
                                userId = peminjaman.userId,
                                namaBarang = barang?.nama?.takeIf { it.isNotBlank() }
                                    ?: peminjaman.namaBarang.ifBlank { "Barang" },
                                fotoBarangUrl = barang?.fotoUrl.orEmpty(),
                                tanggalPinjam = peminjaman.tanggalPinjam,
                                tanggalKembali = peminjaman.tanggalKembali,
                                status = peminjaman.status
                            )
                        )
                    }

                    _daftarPeminjaman.value = uiItems
                    _isLoading.value = false
                }
        }
    }
}
