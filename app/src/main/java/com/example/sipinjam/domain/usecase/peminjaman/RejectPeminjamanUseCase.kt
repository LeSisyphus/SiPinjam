package com.example.sipinjam.domain.usecase.peminjaman

import com.example.sipinjam.domain.model.BorrowingStatus
import com.example.sipinjam.domain.repository.PeminjamanRepository

class RejectPeminjamanUseCase(private val repository: PeminjamanRepository) {
    suspend operator fun invoke(id: String) = repository.updateStatus(id, BorrowingStatus.DITOLAK)
}
