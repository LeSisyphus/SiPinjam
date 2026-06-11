package com.example.sipinjam.domain.usecase.barang

import com.example.sipinjam.domain.model.Barang

class SearchBarangUseCase {
    operator fun invoke(items: List<Barang>, query: String): List<Barang> {
        val keyword = query.trim()
        if (keyword.isBlank()) return items
        return items.filter { barang ->
            barang.nama.contains(keyword, ignoreCase = true) ||
                barang.kategori.contains(keyword, ignoreCase = true) ||
                barang.deskripsi.contains(keyword, ignoreCase = true)
        }
    }
}
