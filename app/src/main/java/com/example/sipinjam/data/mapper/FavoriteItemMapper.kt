package com.example.sipinjam.data.mapper

import com.example.sipinjam.data.local.entity.FavoriteItemEntity
import com.example.sipinjam.domain.model.FavoriteItem

fun FavoriteItemEntity.toDomain(): FavoriteItem = FavoriteItem(
    userId = userId,
    barangId = barangId,
    nama = nama,
    kategori = kategori,
    fotoUrl = fotoUrl,
    addedAt = addedAt,
)

fun FavoriteItem.toEntity(): FavoriteItemEntity = FavoriteItemEntity(
    userId = userId,
    barangId = barangId,
    nama = nama,
    kategori = kategori,
    fotoUrl = fotoUrl,
    addedAt = addedAt,
)
