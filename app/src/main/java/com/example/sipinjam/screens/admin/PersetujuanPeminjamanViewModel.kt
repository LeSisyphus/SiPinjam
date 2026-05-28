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

    init {
        muatPeminjaman()
        muatPengembalian()
    }

    fun muatPeminjaman() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = peminjamanRepository.semuaPeminjaman()
            result.onSuccess { list ->
                _daftarPeminjaman.value = list.filter { it.status == "Diproses" }
            }
            _isLoading.value = false
        }
    }

    fun muatPengembalian() {
        viewModelScope.launch {
            val result = pengembalianRepository.semuaPengembalian()
            result.onSuccess { list ->
                _daftarPengembalian.value = list.filter { it.status == "Menunggu Verifikasi" }
            }
        }
    }

    fun setujui(id: String) {
        viewModelScope.launch {
            peminjamanRepository.updateStatus(id, "Disetujui")
            muatPeminjaman()
        }
    }

    fun tolak(id: String) {
        viewModelScope.launch {
            peminjamanRepository.updateStatus(id, "Ditolak")
            muatPeminjaman()
        }
    }
}