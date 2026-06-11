package com.example.sipinjam.domain.usecase.barang

import com.example.sipinjam.domain.repository.BarangRepository

class DeleteBarangUseCase(private val repository: BarangRepository) {
    suspend operator fun invoke(id: String) = repository.deleteBarang(id)
}
