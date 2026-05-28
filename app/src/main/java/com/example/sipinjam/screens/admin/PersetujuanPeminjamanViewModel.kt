package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PengembalianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PersetujuanPeminjamanViewModel(
    private val peminjamanRepository: PeminjamanRepository = PeminjamanRepository(),
    private val pengembalianRepository: PengembalianRepository = PengembalianRepository()
) : ViewModel() {

    private val _daftarPeminjaman = MutableStateFlow<List<Peminjaman>>(emptyList())
    val daftarPeminjaman: StateFlow<List<Peminjaman>> = _daftarPeminjaman.asStateFlow()

    private val _daftarPengembalian = MutableStateFlow<List<Pengembalian>>(emptyList())
    val daftarPengembalian: StateFlow<List<Pengembalian>> = _daftarPengembalian.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        muatPeminjaman()
        muatPengembalian()
    }

    fun muatPeminjaman() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            peminjamanRepository.listenSemuaPeminjaman()
                .catch { exception ->
                    _isLoading.value = false
                    _errorMessage.value = exception.localizedMessage ?: "Gagal memuat data peminjaman"
                }
                .collect { list ->
                    _daftarPeminjaman.value = list.filter { it.status == "Diproses" }
                    _isLoading.value = false
                }
        }
    }

    fun muatPengembalian() {
        viewModelScope.launch {
            val result = pengembalianRepository.semuaPengembalian()

            result
                .onSuccess { list ->
                    _daftarPengembalian.value = list.filter {
                        it.status == "Menunggu Verifikasi"
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.localizedMessage ?: "Gagal memuat data pengembalian"
                }
        }
    }

    fun setujui(id: String) {
        viewModelScope.launch {
            val result = peminjamanRepository.updateStatus(id, "Disetujui")

            result.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage ?: "Gagal menyetujui peminjaman"
            }
        }
    }

    fun tolak(id: String) {
        viewModelScope.launch {
            val result = peminjamanRepository.updateStatus(id, "Ditolak")

            result.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage ?: "Gagal menolak peminjaman"
            }
        }
    }
}