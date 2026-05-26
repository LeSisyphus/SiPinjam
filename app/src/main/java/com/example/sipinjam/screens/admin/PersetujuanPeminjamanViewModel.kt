package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersetujuanPeminjamanViewModel(
    private val repository: PeminjamanRepository = PeminjamanRepository()
) : ViewModel() {

    private val _daftarPeminjaman = MutableStateFlow<List<Peminjaman>>(emptyList())
    val daftarPeminjaman: StateFlow<List<Peminjaman>> = _daftarPeminjaman.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        muatPeminjaman()
    }

    fun muatPeminjaman() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.semuaPeminjaman()
            result.onSuccess { list ->
                _daftarPeminjaman.value = list.filter { it.status == "Diproses" }
            }
            _isLoading.value = false
        }
    }

    fun setujui(id: String) {
        viewModelScope.launch {
            repository.updateStatus(id, "Disetujui")
            muatPeminjaman()
        }
    }

    fun tolak(id: String) {
        viewModelScope.launch {
            repository.updateStatus(id, "Ditolak")
            muatPeminjaman()
        }
    }
}