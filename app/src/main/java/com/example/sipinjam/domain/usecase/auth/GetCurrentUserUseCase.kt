package com.example.sipinjam.domain.usecase.auth

import com.example.sipinjam.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.getCurrentUser()
}
