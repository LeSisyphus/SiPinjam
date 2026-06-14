package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.model.User
import com.example.sipinjam.domain.usecase.auth.GetUserByIdUseCase
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ApprovePeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.GetPeminjamanDetailUseCase
import com.example.sipinjam.domain.usecase.peminjaman.RejectPeminjamanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.sipinjam.utils.UiMessageKey

data class DetailPengajuanUiState(
    val peminjaman: Peminjaman? = null,
    val barang: Barang? = null,
    val peminjam: User? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionDone: Boolean = false,
)

class DetailPengajuanViewModel(
    private val getPeminjamanDetailUseCase: GetPeminjamanDetailUseCase,
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val approvePeminjamanUseCase: ApprovePeminjamanUseCase,
    private val rejectPeminjamanUseCase: RejectPeminjamanUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailPengajuanUiState())
    val uiState: StateFlow<DetailPengajuanUiState> = _uiState.asStateFlow()

    fun loadDetail(peminjamanId: String) {
        if (peminjamanId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = UiMessageKey.INVALID_BORROWING_ID
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

            val peminjamanResult = getPeminjamanDetailUseCase(peminjamanId)

            if (peminjamanResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = UiMessageKey.LOAD_BORROWING_DETAIL_FAILED
                    )
                }
                return@launch
            }

            val peminjaman = peminjamanResult.getOrNull()

            if (peminjaman == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = UiMessageKey.BORROWING_NOT_FOUND
                    )
                }
                return@launch
            }

            val barang = getBarangDetailUseCase(peminjaman.barangId)
            val peminjam = getUserByIdUseCase(peminjaman.userId)

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

            val result = approvePeminjamanUseCase(peminjaman)

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
                        errorMessage = UiMessageKey.APPROVE_BORROWING_FAILED
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

            val result = rejectPeminjamanUseCase(peminjaman.id)

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
                        errorMessage = UiMessageKey.REJECT_BORROWING_FAILED
                    )
                }
            }
        }
    }
}
