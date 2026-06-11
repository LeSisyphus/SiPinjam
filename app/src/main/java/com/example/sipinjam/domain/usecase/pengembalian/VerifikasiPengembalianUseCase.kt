package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.repository.PengembalianRepository

class VerifikasiPengembalianUseCase(private val repository: PengembalianRepository) {
    suspend operator fun invoke(pengembalianId: String, catatanAdmin: String, kondisiBarang: String) =
        repository.setujuiPengembalian(pengembalianId, catatanAdmin, kondisiBarang)
}
