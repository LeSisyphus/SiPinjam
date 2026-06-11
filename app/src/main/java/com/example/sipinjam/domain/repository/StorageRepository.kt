package com.example.sipinjam.domain.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadFotoProfil(uri: Uri): Result<String>
    suspend fun uploadFotoPengembalian(uri: Uri, peminjamanId: String): Result<String>
}
