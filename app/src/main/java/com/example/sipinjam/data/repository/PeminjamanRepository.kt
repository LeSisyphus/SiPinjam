package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.Peminjaman
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PeminjamanRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("borrowings")

    suspend fun tambahPeminjaman(peminjaman: Peminjaman): Result<Unit> {
        return try {
            val docRef = collection.document()
            val data = peminjaman.copy(id = docRef.id)
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPeminjamanByUser(userId: String): Result<List<Peminjaman>> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snapshot.toObjects(Peminjaman::class.java)
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun semuaPeminjaman(): Result<List<Peminjaman>> {
        return try {
            val snapshot = collection
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snapshot.toObjects(Peminjaman::class.java)
            Result.success(list)
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