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

    fun muatPengembalian(pengembalianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPengembalianById(pengembalianId)
            result.onSuccess { _pengembalian.value = it }
            result.onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun verifikasi(pengembalianId: String, catatan: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateStatus(pengembalianId, "Terverifikasi")
            if (result.isSuccess) {
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun tolak(pengembalianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateStatus(pengembalianId, "Ditolak")
            if (result.isSuccess) {
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