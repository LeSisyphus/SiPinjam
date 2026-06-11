package com.example.sipinjam.domain.usecase.profile

import com.example.sipinjam.domain.repository.AuthRepository

class ChangePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(passwordLama: String, passwordBaru: String) =
        repository.updatePassword(passwordLama, passwordBaru)
}
