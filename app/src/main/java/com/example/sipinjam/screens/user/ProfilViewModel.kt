package com.example.sipinjam.screens.user

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.User
import com.example.sipinjam.data.preferences.AppPreferences
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.auth.LogoutUseCase
import com.example.sipinjam.domain.usecase.profile.UpdateProfilePhotoUrlUseCase
import com.example.sipinjam.domain.usecase.profile.UpdateProfileUseCase
import com.example.sipinjam.domain.usecase.storage.UploadProfilePhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.util.Locale

data class ProfilUiState(
    val user: User = User(),
    val namaInput: String = "",
    val nomorTeleponInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingFoto: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedLang: String = "ID",
    val isDarkMode: Boolean = false,
)

class ProfilViewModel(
    application: Application,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateProfilePhotoUrlUseCase: UpdateProfilePhotoUrlUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val logoutUseCase: LogoutUseCase,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfilUiState())
    val uiState: StateFlow<ProfilUiState> = _uiState.asStateFlow()

    init {
        AppPreferences.load(application)
        _uiState.update {
            it.copy(
                selectedLang = AppPreferences.languageCode.uppercase(Locale.ROOT),
                isDarkMode = AppPreferences.isDarkMode
            )
        }
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = getCurrentUserUseCase()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        user = user,
                        namaInput = user.nama,
                        nomorTeleponInput = user.nomorTelepon,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNamaChange(value: String) {
        _uiState.update { it.copy(namaInput = value, errorMessage = null) }
    }

    fun onNomorTeleponChange(value: String) {
        _uiState.update { it.copy(nomorTeleponInput = value, errorMessage = null) }
    }

    fun onLangChange(lang: String) {
        AppPreferences.setLanguage(getApplication(), lang.lowercase(Locale.ROOT))
        _uiState.update { it.copy(selectedLang = AppPreferences.languageCode.uppercase(Locale.ROOT)) }
    }

    fun onDarkModeToggle() {
        val nextValue = !_uiState.value.isDarkMode
        AppPreferences.setDarkMode(getApplication(), nextValue)
        _uiState.update { it.copy(isDarkMode = nextValue) }
    }

    fun onSimpanProfil() {
        val state = _uiState.value
        if (state.namaInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = updateProfileUseCase(state.namaInput, state.nomorTeleponInput)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Profil berhasil disimpan.",
                            user = it.user.copy(
                                nama = it.namaInput,
                                nomorTelepon = it.nomorTeleponInput
                            )
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = e.message ?: "Gagal menyimpan.")
                    }
                }
            )
        }
    }

    fun onFotoSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingFoto = true) }
            val result = uploadProfilePhotoUseCase(uri)
            result.fold(
                onSuccess = { url ->
                    updateProfilePhotoUrlUseCase(url)
                    _uiState.update {
                        it.copy(
                            isUploadingFoto = false,
                            user = it.user.copy(fotoUrl = url)
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isUploadingFoto = false) }
                }
            )
        }
    }

    fun onDismissSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun onLogout(onDone: () -> Unit) {
        logoutUseCase()
        onDone()
    }
}