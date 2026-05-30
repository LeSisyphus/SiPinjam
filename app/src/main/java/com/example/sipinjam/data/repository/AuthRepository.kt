package com.example.sipinjam.data.repository

import com.example.sipinjam.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    // ── Login ────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID tidak ditemukan"))
            val user = getUserFromFirestore(uid)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Register ─────────────────────────────────────────────────────────────

    suspend fun register(
        email: String,
        password: String,
        nama: String,
        peran: String,
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID tidak ditemukan"))

            val user = User(
                uid   = uid,
                nama  = nama,
                email = email,
                peran   = peran,
                role  = "peminjam",
            )

            db.collection("users").document(uid).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Reset Password ───────────────────────────────────────────────────────

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    fun logout() {
        auth.signOut()
    }

    // ── Session ──────────────────────────────────────────────────────────────

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            getUserFromFirestore(uid)
        } catch (e: Exception) {
            null
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private suspend fun getUserFromFirestore(uid: String): User {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)
            ?: throw Exception("Data user tidak ditemukan di Firestore")
    }

    suspend fun updateProfile(nama: String, nomorTelepon: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User tidak ditemukan"))
            db.collection("users").document(uid).update(
                mapOf(
                    "nama" to nama,
                    "nomorTelepon" to nomorTelepon
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(passwordLama: String, passwordBaru: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User tidak ditemukan"))

            // Re-authenticate dulu sebelum ganti password
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email!!, passwordLama)
            user.reauthenticate(credential).await()
            user.updatePassword(passwordBaru).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFotoUrl(fotoUrl: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User tidak ditemukan"))
            db.collection("users").document(uid).update("fotoUrl", fotoUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(uid: String): User? {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.toObject(User::class.java)?.copy(uid = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }
}