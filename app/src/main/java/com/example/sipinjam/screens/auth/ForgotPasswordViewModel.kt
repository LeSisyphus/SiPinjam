package com.example.sipinjam.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class ForgotPasswordViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, isSuccess = false) }
    }

    fun onKirimClick() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.resetPassword(email)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = parseFirebaseError(e.message)
                        )
                    }
                }
            )
        }
    }

    private fun parseFirebaseError(message: String?): String {
        return when {
            message == null                          -> "Terjadi kesalahan, coba lagi."
            message.contains("no user record")       -> "Email tidak terdaftar."
            message.contains("badly formatted")      -> "Format email tidak valid."
            message.contains("network error")        -> "Tidak ada koneksi internet."
            else                                     -> "Gagal mengirim email. Coba lagi."
        }
    }
}