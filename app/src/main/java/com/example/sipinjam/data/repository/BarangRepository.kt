package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BarangRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    fun getAllBarangRealTime(): Flow<List<Barang>> = callbackFlow {
        val listener = itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val barangList = snapshot?.documents?.mapNotNull { document ->
                document.toObject(Barang::class.java)?.copy(id = document.id)
            } ?: emptyList()
            trySend(barangList)
        }
        awaitClose { listener.remove() }
    }

    fun getBarangByIdRealTime(id: String): Flow<Barang?> = callbackFlow {
        val listener = itemsCollection.document(id).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(Barang::class.java)?.copy(id = snapshot.id))
        }
        awaitClose { listener.remove() }
    }

    suspend fun getBarangById(id: String): Barang? {
        return try {
            val document = itemsCollection.document(id).get().await()
            document.toObject(Barang::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addBarang(barang: Barang): Boolean {
        return try {
            val newDocRef = itemsCollection.document()
            val barangWithId = barang.copy(id = newDocRef.id)
            newDocRef.set(barangWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateBarang(barang: Barang): Boolean {
        if (barang.id.isBlank()) return false
        return try {
            itemsCollection.document(barang.id).set(barang).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteBarang(id: String): Boolean {
        return try {
            itemsCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}