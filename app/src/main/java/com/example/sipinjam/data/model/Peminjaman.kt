package com.example.sipinjam.data.model

data class Peminjaman(
    val id: String = "",
    val userId: String = "",
    val namaUser: String = "",
    val barangId: String = "",
    val namaBarang: String = "",
    val tanggalPinjam: String = "",
    val tanggalKembali: String = "",
    val keperluan: String = "",
    val status: String = BorrowingStatus.DIPROSES,
    val createdAt: Long = System.currentTimeMillis(),
)