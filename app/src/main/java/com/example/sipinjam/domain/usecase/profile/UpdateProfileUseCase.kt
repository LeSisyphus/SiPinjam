package com.example.sipinjam.domain.usecase.profile

import com.example.sipinjam.domain.repository.AuthRepository

class UpdateProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(nama: String, nomorTelepon: String) =
        repository.updateProfile(nama, nomorTelepon)
}
