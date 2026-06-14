package com.example.sipinjam.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.sipinjam.utils.UiMessageKey

data class RegisterUiState(
    val peran: String = "Mahasiswa",
    val namaLengkap: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onPeranChange(peran: String) {
        _uiState.update { it.copy(peran = peran, errorMessage = null) }
    }

    fun onNamaChange(value: String) {
        _uiState.update { it.copy(namaLengkap = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.namaLengkap.isBlank()) {
            _uiState.update { it.copy(errorMessage = UiMessageKey.EMPTY_FULL_NAME) }
            return
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = UiMessageKey.EMPTY_EMAIL) }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = UiMessageKey.PASSWORD_MIN_6) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = registerUseCase(
                email = state.email,
                password = state.password,
                nama = state.namaLengkap,
                peran = state.peran,
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
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
            message == null                           -> UiMessageKey.TRY_AGAIN
            message.contains("email address is already") -> UiMessageKey.EMAIL_ALREADY_REGISTERED
            message.contains("badly formatted")       -> UiMessageKey.INVALID_EMAIL_FORMAT
            message.contains("network error")         -> UiMessageKey.NO_INTERNET
            else                                      -> UiMessageKey.REGISTER_FAILED
        }
    }
}
