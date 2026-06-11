package com.example.sipinjam.domain.usecase.pengembalian

import com.example.sipinjam.domain.repository.PengembalianRepository

class ObservePengembalianUseCase(private val repository: PengembalianRepository) {
    operator fun invoke() = repository.listenSemuaPengembalian()
}
