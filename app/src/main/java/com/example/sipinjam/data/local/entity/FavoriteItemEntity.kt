package com.example.sipinjam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItemEntity(
    @PrimaryKey
    val barangId: String,
    val nama: String,
    val kategori: String,
    val fotoUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)
