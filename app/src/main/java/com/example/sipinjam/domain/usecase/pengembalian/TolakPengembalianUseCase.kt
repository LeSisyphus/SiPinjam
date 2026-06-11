package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.repository.PengembalianRepository

class TolakPengembalianUseCase(private val repository: PengembalianRepository) {
    suspend operator fun invoke(pengembalianId: String, catatanAdmin: String) =
        repository.tolakPengembalian(pengembalianId, catatanAdmin)
}
