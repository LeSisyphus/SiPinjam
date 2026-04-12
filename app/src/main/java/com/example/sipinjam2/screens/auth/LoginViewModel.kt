package com.example.sipinjam2.ui.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val selectedLang: String = "ID",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel : ViewModel() {

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

    fun onLoginClick(
        onSuccess: (isAdmin: Boolean) -> Unit,
    ) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan password tidak boleh kosong.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val isAdmin = state.email.contains("admin")
        _uiState.update { it.copy(isLoading = false) }
        onSuccess(isAdmin)
    }
}