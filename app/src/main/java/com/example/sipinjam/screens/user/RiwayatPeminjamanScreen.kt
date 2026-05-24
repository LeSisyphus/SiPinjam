package com.example.sipinjam.screens.user

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.ui.components.UserBottomNavBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusBlue
import com.example.sipinjam.ui.theme.StatusBlueBg
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusGreenBg
import com.example.sipinjam.ui.theme.StatusOrange
import com.example.sipinjam.ui.theme.StatusOrangeBg
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.StatusRedLightBg
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary

@Composable
fun RiwayatPeminjamanScreen(
    onBackClick: () -> Unit = {},
    onBerandaClick: () -> Unit = {},
    onKatalogClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
    onPengembalianClick: (namaBarang: String, tanggalPinjam: String, tanggalJatuhTempo: String) -> Unit = { _, _, _ -> },
    viewModel: RiwayatPeminjamanViewModel = viewModel()
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filterAktif by remember { mutableStateOf("Semua") }
    val filterList = listOf("Semua", "Diproses", "Disetujui", "Ditolak", "Dikembalikan")

    val filtered = if (filterAktif == "Semua") {
        daftarPeminjaman
    } else {
        daftarPeminjaman.filter { it.status == filterAktif }
    }

    LaunchedEffect(Unit) {
        viewModel.muatRiwayat()
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Riwayat Peminjaman",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
                selectedTabIndex = filterList.indexOf(filterAktif),
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SiPinjamBlue)
                }
            } else if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Belum ada riwayat peminjaman", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { peminjaman ->
                        RiwayatCard(
                            peminjaman = peminjaman,
                            onClick = {
                                onPengembalianClick(
                                    peminjaman.namaBarang,
                                    peminjaman.tanggalPinjam,
                                    peminjaman.tanggalKembali
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatCard(
    peminjaman: Peminjaman,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
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
            ) { }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = peminjaman.namaBarang,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
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
                        text = "${peminjaman.tanggalPinjam} - ${peminjaman.tanggalKembali}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                StatusBadge(status = peminjaman.status)
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Diproses"     -> Pair(StatusOrangeBg, StatusOrange)
        "Disetujui"    -> Pair(StatusGreenBg, StatusGreen)
        "Ditolak"      -> Pair(StatusRedLightBg, StatusRed)
        "Dikembalikan" -> Pair(StatusBlueBg, StatusBlue)
        else           -> Pair(InputBg, TextSecondary)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RiwayatPeminjamanScreenPreview() {
    MaterialTheme {
        RiwayatPeminjamanScreen()
    }
}