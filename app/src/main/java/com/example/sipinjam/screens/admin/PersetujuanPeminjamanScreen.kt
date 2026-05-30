package com.example.sipinjam.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sipinjam.R
import com.example.sipinjam.ui.components.AdminBottomNavBar
import com.example.sipinjam.ui.theme.*

@Composable
fun PersetujuanPeminjamanScreen(
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
    onDetailPengajuanClick: (peminjamanId: String) -> Unit = {},
    onVerifikasiClick: (pengembalianId: String) -> Unit = {},
    viewModel: PersetujuanPeminjamanViewModel = viewModel()
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val daftarPengembalian by viewModel.daftarPengembalian.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var tabAktif by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_persetujuan),
        stringResource(R.string.tab_pengembalian)
    )

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 2.dp
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = SiPinjamBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    TabRow(
                        selectedTabIndex = tabAktif,
                        containerColor = CardWhite,
                        contentColor = SiPinjamBlue,
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = tabAktif == index,
                                onClick = { tabAktif = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (tabAktif == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                },
                                selectedContentColor = SiPinjamBlue,
                                unselectedContentColor = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AdminBottomNavBar(
                selected = 2,
                onDashboardClick = onDashboardClick,
                onBarangClick = onBarangClick,
                onPermintaanClick = onPermintaanClick,
                onProfilClick = onProfilClick
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SiPinjamBlue)
            }
        } else {
            when (tabAktif) {
                0 -> {
                    if (daftarPeminjaman.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.empty_permohonan),
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(daftarPeminjaman, key = { it.id }) { item ->
                                PermohonanCard(
                                    namaUser = item.namaUser,
                                    namaBarang = item.namaBarang,
                                    tanggal = "${item.tanggalPinjam} - ${item.tanggalKembali}",
                                    onDetailClick = {
                                        onDetailPengajuanClick(item.id)
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (daftarPengembalian.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada pengembalian menunggu verifikasi",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(daftarPengembalian, key = { it.id }) { item ->
                                PengembalianCard(
                                    tanggalKembali = item.tanggalKembali,
                                    catatan        = item.catatan,
                                    onClick        = { onVerifikasiClick(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PengembalianCard(
    tanggalKembali: String,
    catatan: String,
    onClick: () -> Unit
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkImageBg)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tanggalKembali,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (catatan.isNotBlank()) {
                    Text(
                        text = catatan,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StatusOrangeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Menunggu Verifikasi",
                        color = StatusOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PermohonanCard(
    namaUser: String,
    namaBarang: String,
    tanggal: String,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(DarkImageBg)
                )

                Text(
                    text = namaUser,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkImageBg)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = namaBarang,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = tanggal,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = onDetailClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue)
            ) {
                Text(
                    text = "Lihat Detail Pengajuan",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PersetujuanPeminjamanScreenPreview() {
    MaterialTheme {
        PersetujuanPeminjamanScreen()
    }
}