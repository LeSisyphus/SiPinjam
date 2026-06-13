package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.repository.PengembalianRepository

class GetPengembalianByPeminjamanIdUseCase(private val repository: PengembalianRepository) {
    suspend operator fun invoke(peminjamanId: String) = repository.getPengembalianByPeminjamanId(peminjamanId)
}
