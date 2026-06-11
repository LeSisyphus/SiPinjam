package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import com.example.sipinjam.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GantiPasswordUiState(
    val passwordLama: String = "",
    val passwordBaru: String = "",
    val konfirmasiPassword: String = "",
    val passwordLamaVisible: Boolean = false,
    val passwordBaruVisible: Boolean = false,
    val konfirmasiVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class GantiPasswordViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GantiPasswordUiState())
    val uiState: StateFlow<GantiPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordLamaChange(value: String) {
        _uiState.update { it.copy(passwordLama = value, errorMessage = null) }
    }

    fun onPasswordBaruChange(value: String) {
        _uiState.update { it.copy(passwordBaru = value, errorMessage = null) }
    }

    fun onKonfirmasiChange(value: String) {
        _uiState.update { it.copy(konfirmasiPassword = value, errorMessage = null) }
    }

    fun onTogglePasswordLama() {
        _uiState.update { it.copy(passwordLamaVisible = !it.passwordLamaVisible) }
    }

    fun onTogglePasswordBaru() {
        _uiState.update { it.copy(passwordBaruVisible = !it.passwordBaruVisible) }
    }

    fun onToggleKonfirmasi() {
        _uiState.update { it.copy(konfirmasiVisible = !it.konfirmasiVisible) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun onSimpanClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.passwordLama.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password saat ini tidak boleh kosong.") }
            return
        }
        if (state.passwordBaru.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password baru minimal 8 karakter.") }
            return
        }
        if (state.passwordBaru != state.konfirmasiPassword) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi password tidak cocok.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.updatePassword(state.passwordLama, state.passwordBaru)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = parseError(e.message)
                        )
                    }
                }
            )
        }
    }

    private fun parseError(message: String?): String {
        return when {
            message == null                              -> "Terjadi kesalahan, coba lagi."
            message.contains("invalid credential") ||
                    message.contains("wrong-password")          -> "Password saat ini salah."
            message.contains("network error")           -> "Tidak ada koneksi internet."
            else                                        -> "Gagal mengganti password. Coba lagi."
        }
    }
}