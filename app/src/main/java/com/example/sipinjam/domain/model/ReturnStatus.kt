package com.example.sipinjam.domain.model

/** Firestore return status values. */
object ReturnStatus {
    const val MENUNGGU_VERIFIKASI = "Menunggu Verifikasi"
    const val TERVERIFIKASI = "Terverifikasi"
    const val DITOLAK = "Ditolak"

    fun isWaitingVerification(status: String): Boolean =
        status.equals(MENUNGGU_VERIFIKASI, ignoreCase = true)
}
