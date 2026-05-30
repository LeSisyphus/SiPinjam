package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PengembalianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerifikasiPengembalianViewModel : ViewModel() {

    private val repository = PengembalianRepository()
    private val barangRepository = BarangRepository()
    private val peminjamanRepository = PeminjamanRepository()

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
            val result = repository.getPengembalianById(pengembalianId)
            result.onSuccess {
                _pengembalian.value = it
                if (it.status == "Ditolak") {
                    _catatanTolak.value = it.catatanAdmin
                }
            }
            result.onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun verifikasi(pengembalianId: String, catatan: String, kondisi: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val data = _pengembalian.value

            val result = repository.updateVerifikasi(
                id            = pengembalianId,
                status        = "Terverifikasi",
                catatanAdmin  = catatan,
                kondisiBarang = kondisi
            )

            if (result.isSuccess) {
                if (data != null) {
                    val barang = barangRepository.getBarangById(data.barangId)
                    if (barang != null && data.status != "Terverifikasi") {
                        val stokBaru = barang.stok + 1
                        barangRepository.updateBarang(barang.copy(stok = stokBaru, tersedia = true))
                    }
                }
                data?.peminjamanId?.let {
                    peminjamanRepository.updateStatus(it, "Selesai")
                }
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun tolak(pengembalianId: String, catatan: String) {
        viewModelScope.launch {
            if (catatan.isBlank()) {
                _errorMessage.value = "Catatan admin wajib diisi saat menolak pengembalian"
                return@launch
            }
            _isLoading.value = true
            val data = _pengembalian.value

            val result = repository.updateVerifikasi(
                id            = pengembalianId,
                status        = "Ditolak",
                catatanAdmin  = catatan,
                kondisiBarang = ""
            )

            if (result.isSuccess) {
                data?.peminjamanId?.let {
                    peminjamanRepository.updateStatus(it, "Dipinjam")
                }
                _catatanTolak.value = catatan
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }
}