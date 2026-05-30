package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.Pengembalian
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

    suspend fun getPengembalianById(id: String): Result<Pengembalian> {
        return try {
            val doc = collection.document(id).get().await()
            val pengembalian = doc.toObject(Pengembalian::class.java)
                ?: return Result.failure(Exception("Data tidak ditemukan"))
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
            val pengembalian = snapshot.toObjects(Pengembalian::class.java).firstOrNull()
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

    fun listenSemuaPengembalian(): Flow<List<Pengembalian>> = callbackFlow {
        val listener: ListenerRegistration = collection
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Pengembalian::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}