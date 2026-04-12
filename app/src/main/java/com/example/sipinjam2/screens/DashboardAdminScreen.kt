package com.example.sipinjam2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam2.ui.theme.BackgroundGray
import com.example.sipinjam2.ui.theme.CardWhite
import com.example.sipinjam2.ui.theme.InputBg
import com.example.sipinjam2.ui.theme.SipinjamBlue
import com.example.sipinjam2.ui.theme.StatusGreen
import com.example.sipinjam2.ui.theme.StatusGreenBg
import com.example.sipinjam2.ui.theme.StatusOrange
import com.example.sipinjam2.ui.theme.StatusOrangeBg
import com.example.sipinjam2.ui.theme.TextPrimary
import com.example.sipinjam2.ui.theme.TextSecondary


data class PermintaanItem(
    val nama: String,
    val namaBarang: String,
    val waktu: String,
)

@Composable
fun DashboardAdminScreen(
    jumlahPermintaanMasuk: Int = 12,
    jumlahTersedia: Int = 84,
    jumlahDipinjam: Int = 5,
    permintaanTerbaru: List<PermintaanItem> = listOf(
        PermintaanItem("Budi Darmawan", "Proyektor Epson X500", "Today, 09:15 AM"),
        PermintaanItem("Siti Aminah", "Kamera Sony Alpha A7", "Today, 08:30 AM"),
        PermintaanItem("Andi Wijaya", "Pointer Wireless R400", "Yesterday, 04:45 PM"),
        PermintaanItem("Andi Wijaya", "Pointer Wireless R400", "Yesterday, 04:45 PM"),
    ),
    onLihatSemua: () -> Unit = {},
    onTinjau: (PermintaanItem) -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
) {
    var selectedNav by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AdminBottomNavBar(
                selected = selectedNav,
                onDashboardClick  = { selectedNav = 0; onDashboardClick() },
                onBarangClick     = { selectedNav = 1; onBarangClick() },
                onPermintaanClick = { selectedNav = 2; onPermintaanClick() },
                onProfilClick     = { selectedNav = 3; onProfilClick() },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    tint = SipinjamBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SiPinjam",
                    color = SipinjamBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Card Permintaan Masuk
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Permintaan Masuk",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = jumlahPermintaanMasuk.toString(),
                            color = TextPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(InputBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RequestPage,
                            contentDescription = null,
                            tint = SipinjamBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Card Tersedia & Dipinjam
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    label = "TERSEDIA",
                    jumlah = jumlahTersedia,
                    icon = Icons.Filled.CheckCircle,
                    iconColor = StatusGreen,
                    iconBgColor = StatusGreenBg,
                    modifier = Modifier.weight(1f)
                )
                StatusCard(
                    label = "DIPINJAM",
                    jumlah = jumlahDipinjam,
                    icon = Icons.Filled.Timer,
                    iconColor = StatusOrange,
                    iconBgColor = StatusOrangeBg,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Header Permintaan Terbaru
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Permintaan Terbaru",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lihat Semua",
                    color = SipinjamBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onLihatSemua() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // List Permintaan
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                permintaanTerbaru.forEach { item ->
                    PermintaanCard(
                        item = item,
                        onTinjau = { onTinjau(item) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusCard(
    label: String,
    jumlah: Int,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = jumlah.toString(),
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun PermintaanCard(
    item: PermintaanItem,
    onTinjau: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .background(InputBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nama,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.namaBarang,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = item.waktu,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SipinjamBlue)
                .clickable { onTinjau() }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = "Tinjau",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AdminBottomNavBar(
    selected: Int,
    onDashboardClick: () -> Unit,
    onBarangClick: () -> Unit,
    onPermintaanClick: () -> Unit,
    onProfilClick: () -> Unit,
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = onDashboardClick,
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            label = { Text("DASHBOARD", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = SipinjamBlue,
                selectedTextColor   = SipinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = onBarangClick,
            icon = { Icon(Icons.Filled.Inventory, contentDescription = null) },
            label = { Text("BARANG", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = SipinjamBlue,
                selectedTextColor   = SipinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = onPermintaanClick,
            icon = { Icon(Icons.Filled.RequestPage, contentDescription = null) },
            label = { Text("PERMINTAAN", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = SipinjamBlue,
                selectedTextColor   = SipinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 3,
            onClick = onProfilClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("PROFIL", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = SipinjamBlue,
                selectedTextColor   = SipinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor      = CardWhite
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DashboardAdminScreenPreview() {
    MaterialTheme {
        DashboardAdminScreen()
    }
}