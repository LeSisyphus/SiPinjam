package com.example.sipinjam.domain.usecase.barang

import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.repository.BarangRepository

class AddBarangUseCase(private val repository: BarangRepository) {
    suspend operator fun invoke(barang: Barang) = repository.addBarang(barang)
}
