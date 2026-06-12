package com.example.sipinjam.domain.model

data class FavoriteItem(
    val userId: String,
    val barangId: String,
    val nama: String,
    val kategori: String,
    val fotoUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)
