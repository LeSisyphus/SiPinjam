package com.example.sipinjam.screens.user

import com.example.sipinjam.data.model.BorrowingStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BerandaUiState(
    val barangTersedia: List<BarangTersedia> = emptyList(),
    val itemDikembalikan: List<ItemDikembalikan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class BerandaUserViewModel : ViewModel() {

    private val barangRepository = BarangRepository()
    private val peminjamanRepository = PeminjamanRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    init {
        fetchBarangRealTime()
        fetchItemPerluDikembalikanRealTime()
    }

    private fun fetchBarangRealTime() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            barangRepository.getAllBarangRealTime()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Gagal memuat barang"
                        )
                    }
                }
                .collect { listBarangFirestore ->
                    val barangTersediaMapped = listBarangFirestore
                        .filter { it.stok > 0 }
                        .map { barangDoc ->
                            BarangTersedia(
                                id = barangDoc.id,
                                nama = barangDoc.nama,
                                kategori = barangDoc.kategori.uppercase(),
                                imageUrl = barangDoc.fotoUrl
                            )
                        }

                    _uiState.update {
                        it.copy(
                            barangTersedia = barangTersediaMapped,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun fetchItemPerluDikembalikanRealTime() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()

            if (currentUser == null) {
                _uiState.update {
                    it.copy(errorMessage = "User tidak ditemukan, silakan login ulang")
                }
                return@launch
            }

            peminjamanRepository.listenPeminjamanByUser(currentUser.uid)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            errorMessage = exception.localizedMessage ?: "Gagal memuat data peminjaman"
                        )
                    }
                }
                .collect { daftarPeminjaman ->
                    val daftarPerluDikembalikan = daftarPeminjaman
                        .filter { peminjaman ->
                            BorrowingStatus.canRequestReturn(peminjaman.status)
                        }
                        .map { peminjaman ->
                            async {
                                val barang = barangRepository.getBarangById(peminjaman.barangId)

                                ItemDikembalikan(
                                    peminjamanId = peminjaman.id,
                                    barangId = peminjaman.barangId,
                                    userId = peminjaman.userId,
                                    nama = barang?.nama?.takeIf { it.isNotBlank() }
                                        ?: peminjaman.namaBarang.ifBlank { "Barang" },
                                    lokasi = barang?.lokasi?.takeIf { it.isNotBlank() } ?: "-",
                                    tanggalPinjam = peminjaman.tanggalPinjam,
                                    tanggalJatuhTempo = peminjaman.tanggalKembali,
                                    imageUrl = barang?.fotoUrl.orEmpty()
                                )
                            }
                        }
                        .awaitAll()

                    _uiState.update {
                        it.copy(itemDikembalikan = daftarPerluDikembalikan)
                    }
                }
        }
    }
}