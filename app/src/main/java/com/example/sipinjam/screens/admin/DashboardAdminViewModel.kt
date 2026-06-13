package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.BorrowingStatus
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.usecase.auth.GetUserByIdUseCase
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.barang.ObserveBarangListUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ObservePermintaanPeminjamanUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardAdminUiState(
    val jumlahPermintaanMasuk: Int = 0,
    val jumlahTersedia: Int = 0,
    val jumlahDipinjam: Int = 0,
    val permintaanTerbaru: List<PermintaanItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardAdminViewModel(
    private val observeBarangListUseCase: ObserveBarangListUseCase,
    private val observePermintaanPeminjamanUseCase: ObservePermintaanPeminjamanUseCase,
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardAdminUiState(isLoading = true))
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()

    init {
        listenStatistikBarang()
        listenPeminjaman()
    }

    private fun listenStatistikBarang() {
        viewModelScope.launch {
            observeBarangListUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Gagal memuat data barang"
                        )
                    }
                }
                .collect { daftarBarang ->
                    val tersediaCount = daftarBarang.count { barang ->
                        barang.tersedia && barang.stok > 0
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
    }

    private fun listenPeminjaman() {
        viewModelScope.launch {
            observePermintaanPeminjamanUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Gagal memuat data peminjaman"
                        )
                    }
                }
                .collect { semuaPeminjaman ->
                    val permintaanMasuk = semuaPeminjaman.filter { peminjaman ->
                        peminjaman.status.equals(BorrowingStatus.DIPROSES, ignoreCase = true)
                    }

                    val dipinjamCount = semuaPeminjaman.count { peminjaman ->
                        BorrowingStatus.isBorrowed(peminjaman.status)
                    }

                    val permintaanTerbaru = permintaanMasuk
                        .sortedByDescending { it.createdAt }
                        .take(5)
                        .map { peminjaman ->
                            async { peminjaman.toPermintaanItem() }
                        }
                        .awaitAll()

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

    private suspend fun Peminjaman.toPermintaanItem(): PermintaanItem {
        val user = userId.takeIf { it.isNotBlank() }?.let { getUserByIdUseCase(it) }
        val barang = barangId.takeIf { it.isNotBlank() }?.let { getBarangDetailUseCase(it) }

        return PermintaanItem(
            id = id,
            nama = user?.nama?.takeIf { it.isNotBlank() }
                ?: namaUser.takeIf { it.isNotBlank() }
                ?: "-",
            namaBarang = barang?.nama?.takeIf { it.isNotBlank() }
                ?: namaBarang.takeIf { it.isNotBlank() }
                ?: "-",
            waktu = tanggalPinjam.ifBlank { "-" },
            fotoUserUrl = user?.fotoUrl.orEmpty()
        )
    }

    fun onTinjau(item: PermintaanItem) {
        // Navigasi tetap ditangani oleh DashboardAdminScreen lewat callback onTinjau(item).
        // item.id sudah berisi document id peminjaman untuk membuka detail pengajuan.
    }
}
