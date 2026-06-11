package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.Pengembalian
import kotlinx.coroutines.flow.Flow

interface PengembalianRepository {
    suspend fun tambahPengembalian(pengembalian: Pengembalian): Result<Unit>
    suspend fun ajukanPengembalianDanUpdatePeminjaman(pengembalian: Pengembalian): Result<Unit>
    suspend fun getPengembalianById(id: String): Result<Pengembalian>
    suspend fun getPengembalianByPeminjamanId(peminjamanId: String): Result<Pengembalian?>
    suspend fun updateVerifikasi(id: String, status: String, catatanAdmin: String, kondisiBarang: String): Result<Unit>
    suspend fun setujuiPengembalian(pengembalianId: String, catatanAdmin: String, kondisiBarang: String): Result<Unit>
    suspend fun tolakPengembalian(pengembalianId: String, catatanAdmin: String): Result<Unit>
    fun listenSemuaPengembalian(): Flow<List<Pengembalian>>
}
