package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.Peminjaman
import kotlinx.coroutines.flow.Flow

interface PeminjamanRepository {
    suspend fun tambahPeminjaman(peminjaman: Peminjaman): Result<Unit>
    suspend fun getPeminjamanByUser(userId: String): Result<List<Peminjaman>>
    suspend fun semuaPeminjaman(): Result<List<Peminjaman>>
    suspend fun updateStatus(id: String, statusBaru: String): Result<Unit>
    fun listenPeminjamanByUser(userId: String): Flow<List<Peminjaman>>
    fun listenSemuaPeminjaman(): Flow<List<Peminjaman>>
    suspend fun getPeminjamanById(id: String): Result<Peminjaman>
    suspend fun setujuiDenganKurangiStok(peminjaman: Peminjaman): Result<Unit>
}
