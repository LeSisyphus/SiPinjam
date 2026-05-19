package com.example.sipinjam.data.model

data class Barang(
    val id: String = "",
    val nama: String = "",
    val kategori: String = "",
    val stok: Int = 0,
    val tersedia: Boolean = true,
    val kondisi: String = "",
    val lokasi: String = "",
    val maksimalPinjam: String = "",
    val deskripsi: String = "",
    val fotoUrl: String = "",
)