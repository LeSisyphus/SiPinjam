package com.example.sipinjam.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_items",
    primaryKeys = ["userId", "barangId"],
)
data class FavoriteItemEntity(
    val userId: String,
    val barangId: String,
    val nama: String,
    val kategori: String,
    val fotoUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)
