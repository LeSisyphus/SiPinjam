package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.BorrowingStatus
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.model.ReturnStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PengembalianRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("returns")

    suspend fun tambahPengembalian(pengembalian: Pengembalian): Result<Unit> {
        return try {
            val docRef = collection.document()
            val data = pengembalian.copy(id = docRef.id)
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ajukanPengembalianDanUpdatePeminjaman(
        pengembalian: Pengembalian
    ): Result<Unit> {
        return try {
            val pengembalianRef = collection.document()
            val peminjamanRef = db.collection("borrowings").document(pengembalian.peminjamanId)

            db.runTransaction { transaction ->
                val peminjamanSnapshot = transaction.get(peminjamanRef)

                if (!peminjamanSnapshot.exists()) {
                    throw IllegalStateException("Data peminjaman tidak ditemukan")
                }

                val dataPengembalian = pengembalian.copy(
                    id = pengembalianRef.id,
                    status = ReturnStatus.MENUNGGU_VERIFIKASI
                )

                transaction.set(pengembalianRef, dataPengembalian)
                transaction.update(peminjamanRef, "status", BorrowingStatus.MENUNGGU_VERIFIKASI)

                Unit
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPengembalianById(id: String): Result<Pengembalian> {
        return try {
            val doc = collection.document(id).get().await()

            val pengembalian = doc.toObject(Pengembalian::class.java)
                ?.copy(id = doc.id)
                ?: return Result.failure(Exception("Data pengembalian tidak ditemukan"))

            Result.success(pengembalian)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPengembalianByPeminjamanId(peminjamanId: String): Result<Pengembalian?> {
        return try {
            val snapshot = collection
                .whereEqualTo("peminjamanId", peminjamanId)
                .get()
                .await()

            val pengembalian = snapshot.documents
                .mapNotNull { document ->
                    document.toObject(Pengembalian::class.java)?.copy(id = document.id)
                }
                .sortedByDescending { it.createdAt }
                .firstOrNull()

            Result.success(pengembalian)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVerifikasi(
        id: String,
        status: String,
        catatanAdmin: String,
        kondisiBarang: String
    ): Result<Unit> {
        return try {
            collection.document(id)
                .update(
                    mapOf(
                        "status" to status,
                        "catatanAdmin" to catatanAdmin,
                        "kondisiBarang" to kondisiBarang
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setujuiPengembalian(
        pengembalianId: String,
        catatanAdmin: String,
        kondisiBarang: String
    ): Result<Unit> {
        return try {
            val pengembalianRef = collection.document(pengembalianId)

            db.runTransaction { transaction ->
                val pengembalianSnapshot = transaction.get(pengembalianRef)

                if (!pengembalianSnapshot.exists()) {
                    throw IllegalStateException("Data pengembalian tidak ditemukan")
                }

                val pengembalian = pengembalianSnapshot.toObject(Pengembalian::class.java)
                    ?.copy(id = pengembalianSnapshot.id)
                    ?: throw IllegalStateException("Data pengembalian tidak valid")

                if (!pengembalian.status.equals(ReturnStatus.MENUNGGU_VERIFIKASI, ignoreCase = true)) {
                    throw IllegalStateException("Pengembalian ini sudah diproses")
                }

                val peminjamanRef = db.collection("borrowings").document(pengembalian.peminjamanId)
                val barangRef = db.collection("items").document(pengembalian.barangId)

                val peminjamanSnapshot = transaction.get(peminjamanRef)
                val barangSnapshot = transaction.get(barangRef)

                if (!peminjamanSnapshot.exists()) {
                    throw IllegalStateException("Data peminjaman tidak ditemukan")
                }

                if (!barangSnapshot.exists()) {
                    throw IllegalStateException("Data barang tidak ditemukan")
                }

                val stokSaatIni = barangSnapshot.getLong("stok")?.toInt() ?: 0
                val stokBaru = stokSaatIni + 1

                transaction.update(
                    pengembalianRef,
                    mapOf(
                        "status" to ReturnStatus.TERVERIFIKASI,
                        "catatanAdmin" to catatanAdmin,
                        "kondisiBarang" to kondisiBarang
                    )
                )

                transaction.update(
                    peminjamanRef,
                    "status",
                    BorrowingStatus.SELESAI
                )

                transaction.update(
                    barangRef,
                    mapOf(
                        "stok" to stokBaru,
                        "tersedia" to true
                    )
                )

                Unit
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun tolakPengembalian(
        pengembalianId: String,
        catatanAdmin: String
    ): Result<Unit> {
        return try {
            if (catatanAdmin.isBlank()) {
                return Result.failure(Exception("Catatan admin wajib diisi saat menolak pengembalian"))
            }

            val pengembalianRef = collection.document(pengembalianId)

            db.runTransaction { transaction ->
                val pengembalianSnapshot = transaction.get(pengembalianRef)

                if (!pengembalianSnapshot.exists()) {
                    throw IllegalStateException("Data pengembalian tidak ditemukan")
                }

                val pengembalian = pengembalianSnapshot.toObject(Pengembalian::class.java)
                    ?.copy(id = pengembalianSnapshot.id)
                    ?: throw IllegalStateException("Data pengembalian tidak valid")

                if (!pengembalian.status.equals(ReturnStatus.MENUNGGU_VERIFIKASI, ignoreCase = true)) {
                    throw IllegalStateException("Pengembalian ini sudah diproses")
                }

                val peminjamanRef = db.collection("borrowings").document(pengembalian.peminjamanId)
                val peminjamanSnapshot = transaction.get(peminjamanRef)

                if (!peminjamanSnapshot.exists()) {
                    throw IllegalStateException("Data peminjaman tidak ditemukan")
                }

                transaction.update(
                    pengembalianRef,
                    mapOf(
                        "status" to ReturnStatus.DITOLAK,
                        "catatanAdmin" to catatanAdmin,
                        "kondisiBarang" to ""
                    )
                )

                transaction.update(
                    peminjamanRef,
                    "status",
                    BorrowingStatus.DIPINJAM
                )

                Unit
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenSemuaPengembalian(): Flow<List<Pengembalian>> = callbackFlow {
        val listener: ListenerRegistration = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val list = snapshot
                    ?.documents
                    ?.mapNotNull { document ->
                        document.toObject(Pengembalian::class.java)?.copy(id = document.id)
                    }
                    ?: emptyList()

                trySend(list)
            }

        awaitClose { listener.remove() }
    }
}