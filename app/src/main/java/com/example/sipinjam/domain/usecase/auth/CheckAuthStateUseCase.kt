package com.example.sipinjam.domain.usecase.auth

import com.example.sipinjam.domain.repository.AuthRepository

class CheckAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.isLoggedIn()
}
