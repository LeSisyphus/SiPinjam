package com.example.sipinjam.data.model

data class User(
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "peminjam",
    val peran: String = "",
    val fotoUrl: String = "",
    val nomorTelepon: String = ""
)