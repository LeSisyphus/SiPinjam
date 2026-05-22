package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PeminjamanViewModel : ViewModel() {

    private val repository     = PeminjamanRepository()
    private val authRepository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _sukses = MutableStateFlow(false)
    val sukses: StateFlow<Boolean> = _sukses

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun kirimPermohonan(
        barangId: String,
        namaBarang: String,
        tanggalPinjam: String,
        tanggalKembali: String,
        keperluan: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _errorMessage.value = "User tidak ditemukan, silakan login ulang"
                _isLoading.value = false
                return@launch
            }

            val peminjaman = Peminjaman(
                userId        = currentUser.uid,
                namaUser      = currentUser.nama,
                barangId      = barangId,
                namaBarang    = namaBarang,
                tanggalPinjam = tanggalPinjam,
                tanggalKembali = tanggalKembali,
                keperluan     = keperluan,
                status        = "Diproses",
                createdAt     = System.currentTimeMillis()
            )

            val result = repository.tambahPeminjaman(peminjaman)

            _isLoading.value = false
            if (result.isSuccess) {
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengirim permohonan"
            }
        }
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }
}