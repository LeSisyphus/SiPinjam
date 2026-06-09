package com.example.sipinjam.screens.admin

import com.example.sipinjam.data.model.BorrowingStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Barang
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.model.User
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailPengajuanUiState(
    val peminjaman: Peminjaman? = null,
    val barang: Barang? = null,
    val peminjam: User? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionDone: Boolean = false,
)

class DetailPengajuanViewModel : ViewModel() {

    private val peminjamanRepository = PeminjamanRepository()
    private val barangRepository = BarangRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(DetailPengajuanUiState())
    val uiState: StateFlow<DetailPengajuanUiState> = _uiState.asStateFlow()

    fun loadDetail(peminjamanId: String) {
        if (peminjamanId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "ID peminjaman tidak valid"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    actionDone = false
                )
            }

            val peminjamanResult = peminjamanRepository.getPeminjamanById(peminjamanId)

            if (peminjamanResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = peminjamanResult.exceptionOrNull()?.message
                            ?: "Gagal memuat detail peminjaman"
                    )
                }
                return@launch
            }

            val peminjaman = peminjamanResult.getOrNull()

            if (peminjaman == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Data peminjaman tidak ditemukan"
                    )
                }
                return@launch
            }

            val barang = barangRepository.getBarangById(peminjaman.barangId)
            val peminjam = authRepository.getUserById(peminjaman.userId)

            _uiState.update {
                it.copy(
                    peminjaman = peminjaman,
                    barang = barang,
                    peminjam = peminjam,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun setujuiPengajuan() {
        val peminjaman = _uiState.value.peminjaman ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionLoading = true,
                    errorMessage = null
                )
            }

            val result = peminjamanRepository.setujuiDenganKurangiStok(peminjaman)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        actionDone = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Gagal menyetujui pengajuan"
                    )
                }
            }
        }
    }

    fun tolakPengajuan() {
        val peminjaman = _uiState.value.peminjaman ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionLoading = true,
                    errorMessage = null
                )
            }

            val result = peminjamanRepository.updateStatus(
                id = peminjaman.id,
                statusBaru = BorrowingStatus.DITOLAK
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        actionDone = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Gagal menolak pengajuan"
                    )
                }
            }
        }
    }
}