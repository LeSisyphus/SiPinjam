package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RiwayatPeminjamanViewModel : ViewModel() {

    private val repository = PeminjamanRepository()
    private val authRepository = AuthRepository()

    private val _daftarPeminjaman = MutableStateFlow<List<Peminjaman>>(emptyList())
    val daftarPeminjaman: StateFlow<List<Peminjaman>> = _daftarPeminjaman

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun muatRiwayat() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _isLoading.value = false
                return@launch
            }
            _isLoading.value = false
            repository.listenPeminjamanByUser(currentUser.uid).collect { list ->
                _daftarPeminjaman.value = list
            }
        }
    }
}