package com.example.sipinjam.domain.repository

import com.example.sipinjam.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, nama: String, peran: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    fun logout()
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(nama: String, nomorTelepon: String): Result<Unit>
    suspend fun updatePassword(passwordLama: String, passwordBaru: String): Result<Unit>
    suspend fun updateFotoUrl(fotoUrl: String): Result<Unit>
    suspend fun getUserById(uid: String): User?
}
