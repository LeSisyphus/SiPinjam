package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.PengembalianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerifikasiPengembalianViewModel : ViewModel() {

    private val repository = PengembalianRepository()

    private val _pengembalian = MutableStateFlow<Pengembalian?>(null)
    val pengembalian: StateFlow<Pengembalian?> = _pengembalian.asStateFlow()

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

                if (data.status.equals("Ditolak", ignoreCase = true)) {
                    _catatanTolak.value = data.catatanAdmin
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
}