package com.example.sipinjam.domain.usecase.barang

import com.example.sipinjam.domain.repository.BarangRepository

class ObserveBarangListUseCase(private val repository: BarangRepository) {
    operator fun invoke() = repository.getAllBarangRealTime()
}
