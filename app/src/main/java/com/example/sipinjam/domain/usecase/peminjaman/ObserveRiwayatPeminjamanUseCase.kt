package com.example.sipinjam.domain.usecase.peminjaman

import com.example.sipinjam.domain.repository.PeminjamanRepository

class ObserveRiwayatPeminjamanUseCase(private val repository: PeminjamanRepository) {
    operator fun invoke(userId: String) = repository.listenPeminjamanByUser(userId)
}
