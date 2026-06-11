package com.example.sipinjam.domain.model

/**
 * Firestore borrowing status values.
 *
 * Keep these constants centralized so screens, repositories, and view models do
 * not drift into different spellings such as legacy "Disetujui" vs current
 * "Dipinjam".
 */
object BorrowingStatus {
    const val DIPROSES = "Diproses"
    const val DIPINJAM = "Dipinjam"
    const val DISETUJUI_LEGACY = "Disetujui"
    const val MENUNGGU_VERIFIKASI = "Menunggu Verifikasi"
    const val SELESAI = "Selesai"
    const val DITOLAK = "Ditolak"

    fun isPending(status: String): Boolean = status.equals(DIPROSES, ignoreCase = true)

    fun isBorrowed(status: String): Boolean =
        status.equals(DIPINJAM, ignoreCase = true) ||
            status.equals(DISETUJUI_LEGACY, ignoreCase = true) ||
            status.equals(MENUNGGU_VERIFIKASI, ignoreCase = true)

    fun canRequestReturn(status: String): Boolean =
        status.equals(DIPINJAM, ignoreCase = true) ||
            status.equals(DISETUJUI_LEGACY, ignoreCase = true)

    fun isFinished(status: String): Boolean = status.equals(SELESAI, ignoreCase = true)

    fun isRejected(status: String): Boolean = status.equals(DITOLAK, ignoreCase = true)
}
