package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import com.example.sipinjam.domain.model.BorrowingStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.ui.components.SiPinjamTopBar
import com.example.sipinjam.ui.components.StatusChip
import com.example.sipinjam.ui.components.UserBottomNavBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary

import androidx.compose.ui.platform.LocalContext
import com.example.sipinjam.utils.notification.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatPeminjamanScreen(
    onBackClick: () -> Unit = {},
    onBerandaClick: () -> Unit = {},
    onKatalogClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
    onPengembalianClick: (
        peminjamanId: String,
        barangId: String,
        userId: String,
        namaBarang: String,
        tanggalPinjam: String,
        tanggalJatuhTempo: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    viewModel: RiwayatPeminjamanViewModel = viewModel()
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var filterAktif by remember { mutableStateOf("Semua") }
    val filterList = listOf(
        "Semua",
        BorrowingStatus.DIPROSES,
        BorrowingStatus.DISETUJUI_LEGACY,
        BorrowingStatus.DIPINJAM,
        BorrowingStatus.MENUNGGU_VERIFIKASI,
        BorrowingStatus.DITOLAK,
        BorrowingStatus.SELESAI
    )

    val filtered = if (filterAktif == "Semua") {
        daftarPeminjaman
    } else {
        daftarPeminjaman.filter { it.status.equals(filterAktif, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        viewModel.muatRiwayat()
    }

    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    LaunchedEffect(daftarPeminjaman) {
        if (daftarPeminjaman.isNotEmpty()) {
            val itemTerkini = daftarPeminjaman.firstOrNull()
            if (itemTerkini != null) {
                val namaBarang = itemTerkini.namaBarang

                when (itemTerkini.status) {
                    BorrowingStatus.DISETUJUI_LEGACY -> {
                        notificationHelper.showStatusNotification(
                            title = "Peminjaman Disetujui! 🎉",
                            message = "Permintaan pinjam $namaBarang telah disetujui admin. Silakan ambil barang."
                        )
                    }
                    BorrowingStatus.DITOLAK -> {
                        notificationHelper.showStatusNotification(
                            title = "Peminjaman Ditolak ❌",
                            message = "Maaf, permintaan pinjam $namaBarang ditolak oleh admin."
                        )
                    }
                    BorrowingStatus.SELESAI -> {
                        notificationHelper.showStatusNotification(
                            title = "Pengembalian Sukses 🟢",
                            message = "Terima kasih, barang $namaBarang telah sukses dikembalikan ke inventaris."
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            SiPinjamTopBar(
                title = stringResource(R.string.screen_history)
            )
        },
        bottomBar = {
            UserBottomNavBar(
                selected = 2,
                onBerandaClick = onBerandaClick,
                onKatalogClick = onKatalogClick,
                onRiwayatClick = onRiwayatClick,
                onProfilClick = onProfilClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = filterList.indexOf(filterAktif).coerceAtLeast(0),
                containerColor = CardWhite,
                contentColor = SiPinjamBlue,
                edgePadding = 16.dp,
                divider = {}
            ) {
                filterList.forEach { filter ->
                    Tab(
                        selected = filterAktif == filter,
                        onClick = { filterAktif = filter },
                        text = {
                            Text(
                                text = filter,
                                fontSize = 13.sp,
                                fontWeight = if (filterAktif == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = SiPinjamBlue,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SiPinjamBlue)
                    }
                }
                filtered.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = errorMessage ?: "Belum ada riwayat peminjaman",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered, key = { it.id }) { item ->
                            RiwayatCard(
                                item = item,
                                onClick = if (BorrowingStatus.canRequestReturn(item.status)) {
                                    {
                                        onPengembalianClick(
                                            item.id,
                                            item.barangId,
                                            item.userId,
                                            item.namaBarang,
                                            item.tanggalPinjam,
                                            item.tanggalKembali
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatCard(
    item: RiwayatPeminjamanUiItem,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite,
            disabledContainerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkImageBg),
                contentAlignment = Alignment.Center
            ) {
                if (item.fotoBarangUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.fotoBarangUrl,
                        contentDescription = item.namaBarang,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.namaBarang,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${item.tanggalPinjam} - ${item.tanggalKembali}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusChip(status = item.status)
            }

            if (onClick != null) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RiwayatPeminjamanScreenPreview() {
    MaterialTheme {
        RiwayatPeminjamanScreen()
    }
}