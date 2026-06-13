package com.example.sipinjam.utils.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.platform.LocalContext
import com.example.sipinjam.domain.model.BorrowingStatus
import com.example.sipinjam.domain.model.ReturnStatus
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ObservePermintaanPeminjamanUseCase
import com.example.sipinjam.domain.usecase.peminjaman.ObserveRiwayatPeminjamanUseCase
import com.example.sipinjam.domain.usecase.pengembalian.ObservePengembalianUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Global local-notification observer.
 *
 * This composable is installed at the root navigation level, so status
 * notifications are not tied to a single screen such as RiwayatPeminjamanScreen.
 * It still only works while the app process is alive because this is a local
 * notification implementation, not Firebase Cloud Messaging.
 */
@Composable
fun StatusNotificationObserver(
    isLoggedIn: Boolean,
    isAdmin: Boolean,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    observeRiwayatPeminjamanUseCase: ObserveRiwayatPeminjamanUseCase,
    observePermintaanPeminjamanUseCase: ObservePermintaanPeminjamanUseCase,
    observePengembalianUseCase: ObservePengembalianUseCase,
) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    val userBorrowingStatusMap = remember { mutableStateMapOf<String, String>() }
    val adminBorrowingStatusMap = remember { mutableStateMapOf<String, String>() }
    val adminReturnStatusMap = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(isLoggedIn, isAdmin) {
        userBorrowingStatusMap.clear()
        adminBorrowingStatusMap.clear()
        adminReturnStatusMap.clear()

        if (!isLoggedIn) return@LaunchedEffect

        val currentUser = getCurrentUserUseCase()
        val currentUserId = currentUser?.uid.orEmpty()
        if (currentUserId.isBlank()) return@LaunchedEffect

        if (isAdmin) {
            observeAdminNotifications(
                observePermintaanPeminjamanUseCase = observePermintaanPeminjamanUseCase,
                observePengembalianUseCase = observePengembalianUseCase,
                notificationHelper = notificationHelper,
                borrowingStatusMap = adminBorrowingStatusMap,
                returnStatusMap = adminReturnStatusMap,
            )
        } else {
            observeUserBorrowingStatusNotifications(
                userId = currentUserId,
                observeRiwayatPeminjamanUseCase = observeRiwayatPeminjamanUseCase,
                notificationHelper = notificationHelper,
                borrowingStatusMap = userBorrowingStatusMap,
            )
        }
    }
}

private suspend fun observeUserBorrowingStatusNotifications(
    userId: String,
    observeRiwayatPeminjamanUseCase: ObserveRiwayatPeminjamanUseCase,
    notificationHelper: NotificationHelper,
    borrowingStatusMap: MutableMap<String, String>,
) {
    var isInitialLoad = true

    observeRiwayatPeminjamanUseCase(userId)
        .catch { /* Keep notification observer silent if data listener fails. */ }
        .collect { daftarPeminjaman ->
            daftarPeminjaman.forEach { peminjaman ->
                val peminjamanId = peminjaman.id
                val statusSekarang = peminjaman.status
                val statusLama = borrowingStatusMap[peminjamanId]
                val namaBarang = peminjaman.namaBarang

                if (!isInitialLoad && statusLama != null && statusLama != statusSekarang) {
                    when (statusSekarang) {
                        BorrowingStatus.DIPINJAM,
                        BorrowingStatus.DISETUJUI_LEGACY -> {
                            notificationHelper.showStatusNotification(
                                title = "Peminjaman Disetujui!",
                                message = "Permintaan pinjam $namaBarang telah disetujui admin. Silakan ambil barang."
                            )
                        }

                        BorrowingStatus.DITOLAK -> {
                            notificationHelper.showStatusNotification(
                                title = "Peminjaman Ditolak",
                                message = "Maaf, permintaan pinjam $namaBarang ditolak oleh admin."
                            )
                        }

                        BorrowingStatus.SELESAI -> {
                            notificationHelper.showStatusNotification(
                                title = "Pengembalian Sukses",
                                message = "Terima kasih, barang $namaBarang telah sukses dikembalikan ke inventaris."
                            )
                        }
                    }
                }

                borrowingStatusMap[peminjamanId] = statusSekarang
            }

            isInitialLoad = false
        }
}

private suspend fun observeAdminNotifications(
    observePermintaanPeminjamanUseCase: ObservePermintaanPeminjamanUseCase,
    observePengembalianUseCase: ObservePengembalianUseCase,
    notificationHelper: NotificationHelper,
    borrowingStatusMap: MutableMap<String, String>,
    returnStatusMap: MutableMap<String, String>,
) = coroutineScope {
    launch {
        var isInitialBorrowingLoad = true

        observePermintaanPeminjamanUseCase()
            .catch { /* Keep notification observer silent if data listener fails. */ }
            .collect { daftarPeminjaman ->
                daftarPeminjaman.forEach { peminjaman ->
                    val peminjamanId = peminjaman.id
                    val statusSekarang = peminjaman.status
                    val statusLama = borrowingStatusMap[peminjamanId]
                    val isNewPendingRequest = statusLama == null && BorrowingStatus.isPending(statusSekarang)

                    if (!isInitialBorrowingLoad && isNewPendingRequest) {
                        notificationHelper.showStatusNotification(
                            title = "Pengajuan Peminjaman Baru",
                            message = "${peminjaman.namaUser} mengajukan peminjaman ${peminjaman.namaBarang}."
                        )
                    }

                    borrowingStatusMap[peminjamanId] = statusSekarang
                }

                isInitialBorrowingLoad = false
            }
    }

    launch {
        var isInitialReturnLoad = true

        observePengembalianUseCase()
            .catch { /* Keep notification observer silent if data listener fails. */ }
            .collect { daftarPengembalian ->
                daftarPengembalian.forEach { pengembalian ->
                    val pengembalianId = pengembalian.id
                    val statusSekarang = pengembalian.status
                    val statusLama = returnStatusMap[pengembalianId]
                    val isNewReturnRequest = statusLama == null &&
                        ReturnStatus.isWaitingVerification(statusSekarang)

                    if (!isInitialReturnLoad && isNewReturnRequest) {
                        notificationHelper.showStatusNotification(
                            title = "Pengajuan Pengembalian Baru",
                            message = "Ada pengembalian barang yang menunggu verifikasi admin."
                        )
                    }

                    returnStatusMap[pengembalianId] = statusSekarang
                }

                isInitialReturnLoad = false
            }
    }
}
