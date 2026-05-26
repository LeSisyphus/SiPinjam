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
import com.example.sipinjam.R
import com.example.sipinjam.ui.components.AdminBottomNavBar
import com.example.sipinjam.ui.theme.*

data class PermohonanItem(
    val id: String,
    val namaPeminjam: String,
    val nimPeminjam: String,
    val rolePeminjam: String,
    val namaBarang: String,
    val tanggal: String,
)

@Composable
fun PersetujuanPeminjamanScreen(
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
) {
    var tabAktif by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_persetujuan),
        stringResource(R.string.tab_pengembalian)
    )

    val dummyData = listOf(
        PermohonanItem("1", "Aditya Saputra", "#442", stringResource(R.string.role_mahasiswa), "Kamera DSLR Canon EOS R50", "10-13 Apr"),
        PermohonanItem("2", "Dr. Ratna Ningsih", "#108", stringResource(R.string.role_dosen), "Proyektor Epson EB-X51", "11 Apr"),
        PermohonanItem("3", "Bambang Prakoso", "#862", stringResource(R.string.role_staf), "Tripod Kamera Profesional", "12-14 Apr"),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyData) { item ->
                PermohonanCard(
                    item = item,
                    onSetujui = {},
                    onTolak = {}
                )
            }
        }
    }
}

@Composable
fun PermohonanCard(
    item: PermohonanItem,
    onSetujui: () -> Unit,
    onTolak: () -> Unit
) {
    val roleColor = when (item.rolePeminjam) {
        "MAHASISWA" -> Pair(StatusBlueBg, StatusBlue)
        "DOSEN"     -> Pair(StatusGreenBg, StatusGreen)
        "STAF"      -> Pair(StatusOrangeBg, StatusOrange)
        else        -> Pair(InputBg, TextSecondary)
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.namaPeminjam,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = item.nimPeminjam,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(roleColor.first)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.rolePeminjam,
                        color = roleColor.second,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                        text = item.namaBarang,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.tanggal,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onTolak,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed)
                ) {
                    Text(stringResource(R.string.btn_tolak), fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onSetujui,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue)
                ) {
                    Text(stringResource(R.string.btn_setujui), color = Color.White, fontWeight = FontWeight.SemiBold)
                }
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