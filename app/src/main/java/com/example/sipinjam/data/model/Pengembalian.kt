package com.example.sipinjam.data.model

data class Pengembalian(
    val id: String = "",
    val peminjamanId: String = "",
    val userId: String = "",
    val barangId: String = "",
    val fotoKondisiUrl: String = "",
    val catatan: String = "",
    val tanggalKembali: String = "",
    val status: String = "Menunggu Verifikasi",
    val createdAt: Long = System.currentTimeMillis(),
)