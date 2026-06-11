package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.model.Pengembalian
import com.example.sipinjam.domain.repository.PengembalianRepository

class AjukanPengembalianUseCase(private val repository: PengembalianRepository) {
    suspend operator fun invoke(pengembalian: Pengembalian) = repository.ajukanPengembalianDanUpdatePeminjaman(pengembalian)
}
