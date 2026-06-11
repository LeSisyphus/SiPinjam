package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.Barang
import kotlinx.coroutines.flow.Flow

interface BarangRepository {
    fun getAllBarangRealTime(): Flow<List<Barang>>
    suspend fun getBarangById(id: String): Barang?
    suspend fun addBarang(barang: Barang): Boolean
    suspend fun updateBarang(barang: Barang): Boolean
    suspend fun deleteBarang(id: String): Boolean
}
