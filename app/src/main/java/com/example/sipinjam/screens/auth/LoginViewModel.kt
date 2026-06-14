package com.example.sipinjam.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.preferences.AppPreferences
import com.example.sipinjam.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.sipinjam.utils.UiMessageKey

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val selectedLang: String = "ID",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(
    application: Application,
    private val loginUseCase: LoginUseCase,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(selectedLang = AppPreferences.languageCode.uppercase(Locale.ROOT))
        }
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

    fun onLangChange(lang: String) {
        AppPreferences.setLanguage(getApplication(), lang.lowercase(Locale.ROOT))
        _uiState.update { it.copy(selectedLang = AppPreferences.languageCode.uppercase(Locale.ROOT)) }
    }

    fun onLoginClick(onSuccess: (isAdmin: Boolean) -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = UiMessageKey.EMPTY_EMAIL_PASSWORD) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase(state.email, state.password)

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
            message == null                           -> UiMessageKey.TRY_AGAIN
            message.contains("no user record")       -> UiMessageKey.EMAIL_NOT_REGISTERED
            message.contains("password is invalid")  -> UiMessageKey.WRONG_PASSWORD
            message.contains("badly formatted")      -> UiMessageKey.INVALID_EMAIL_FORMAT
            message.contains("blocked all requests") -> UiMessageKey.TOO_MANY_ATTEMPTS
            message.contains("network error")        -> UiMessageKey.NO_INTERNET
            else                                     -> UiMessageKey.LOGIN_FAILED
        }
    }
}
