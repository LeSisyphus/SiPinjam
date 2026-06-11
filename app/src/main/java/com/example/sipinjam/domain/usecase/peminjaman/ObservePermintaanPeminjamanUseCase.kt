package com.example.sipinjam.domain.usecase.peminjaman

import com.example.sipinjam.domain.repository.PeminjamanRepository

class ObservePermintaanPeminjamanUseCase(private val repository: PeminjamanRepository) {
    operator fun invoke() = repository.listenSemuaPeminjaman()
}
