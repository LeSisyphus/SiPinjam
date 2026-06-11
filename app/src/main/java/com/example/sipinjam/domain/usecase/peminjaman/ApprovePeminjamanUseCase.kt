package com.example.sipinjam.domain.usecase.peminjaman

import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.repository.PeminjamanRepository

class ApprovePeminjamanUseCase(private val repository: PeminjamanRepository) {
    suspend operator fun invoke(peminjaman: Peminjaman) = repository.setujuiDenganKurangiStok(peminjaman)
}
