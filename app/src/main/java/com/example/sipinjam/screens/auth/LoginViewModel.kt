package com.example.sipinjam.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val selectedLang: String = "ID",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onLangChange(lang: String) {
        _uiState.update { it.copy(selectedLang = lang) }
    }

    fun onLoginClick(onSuccess: (isAdmin: Boolean) -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan password tidak boleh kosong.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.login(state.email, state.password)

            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(user.role == "admin")
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
            message == null                              -> "Terjadi kesalahan, coba lagi."
            message.contains("no user record")          -> "Email tidak terdaftar."
            message.contains("password is invalid")     -> "Password salah."
            message.contains("badly formatted")         -> "Format email tidak valid."
            message.contains("blocked all requests")    -> "Terlalu banyak percobaan. Coba lagi nanti."
            message.contains("network error")           -> "Tidak ada koneksi internet."
            else                                        -> "Login gagal. Periksa email dan password."
        }
    }
}