package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.repository.PengembalianRepository

class GetPengembalianDetailUseCase(private val repository: PengembalianRepository) {
    suspend operator fun invoke(id: String) = repository.getPengembalianById(id)
}
