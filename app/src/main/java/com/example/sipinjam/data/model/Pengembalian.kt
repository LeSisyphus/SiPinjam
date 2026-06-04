package com.example.sipinjam.data.model

data class Pengembalian(
    val id: String = "",
    val peminjamanId: String = "",
    val userId: String = "",
    val barangId: String = "",
    val fotoKondisiUrl: String = "",
    val catatan: String = "",
    val catatanAdmin: String = "",
    val kondisiBarang: String = "",
    val tanggalKembali: String = "",
    val status: String = ReturnStatus.MENUNGGU_VERIFIKASI,
    val createdAt: Long = System.currentTimeMillis(),
)