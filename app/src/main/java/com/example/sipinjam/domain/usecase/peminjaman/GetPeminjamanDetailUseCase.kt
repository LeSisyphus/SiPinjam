package com.example.sipinjam.domain.usecase.peminjaman

import com.example.sipinjam.domain.repository.PeminjamanRepository

class GetPeminjamanDetailUseCase(private val repository: PeminjamanRepository) {
    suspend operator fun invoke(id: String) = repository.getPeminjamanById(id)
}
