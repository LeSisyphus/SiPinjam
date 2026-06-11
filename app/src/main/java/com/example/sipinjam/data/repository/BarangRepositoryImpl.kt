package com.example.sipinjam.data.repository

import com.example.sipinjam.domain.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import com.example.sipinjam.domain.repository.BarangRepository
class BarangRepositoryImpl : BarangRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    override fun getAllBarangRealTime(): Flow<List<Barang>> = callbackFlow {
        val listener = itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val barangList = snapshot?.documents?.mapNotNull { document ->
                document.toObject(Barang::class.java)?.copy(id = document.id)
            } ?: emptyList()

            trySend(barangList)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getBarangById(id: String): Barang? {
        return try {
            val document = itemsCollection.document(id).get().await()
            document.toObject(Barang::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addBarang(barang: Barang): Boolean {
        return try {
            val newDocRef = itemsCollection.document()
            val barangWithId = barang.copy(id = newDocRef.id)
            newDocRef.set(barangWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateBarang(barang: Barang): Boolean {
        if (barang.id.isBlank()) return false

        return try {
            itemsCollection.document(barang.id).set(barang).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteBarang(id: String): Boolean {
        return try {
            itemsCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
