package com.example.sipinjam.domain.usecase.profile

import com.example.sipinjam.domain.repository.AuthRepository

class UpdateProfilePhotoUrlUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(fotoUrl: String) = repository.updateFotoUrl(fotoUrl)
}
