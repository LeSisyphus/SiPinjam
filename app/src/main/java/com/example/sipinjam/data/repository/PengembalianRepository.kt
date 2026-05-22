package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.Pengembalian
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun getPengembalianByUser(userId: String): Result<List<Pengembalian>> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snapshot.toObjects(Pengembalian::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun semuaPengembalian(): Result<List<Pengembalian>> {
        return try {
            val snapshot = collection
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snapshot.toObjects(Pengembalian::class.java)
            Result.success(list)
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

    suspend fun updateStatus(id: String, statusBaru: String): Result<Unit> {
        return try {
            collection.document(id)
                .update("status", statusBaru)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}