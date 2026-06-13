package com.example.sipinjam.domain.usecase.auth

import com.example.sipinjam.domain.repository.AuthRepository

class GetUserByIdUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(uid: String) = repository.getUserById(uid)
}
