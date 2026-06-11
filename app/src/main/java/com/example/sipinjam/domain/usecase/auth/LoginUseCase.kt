package com.example.sipinjam.domain.usecase.auth

import com.example.sipinjam.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}
