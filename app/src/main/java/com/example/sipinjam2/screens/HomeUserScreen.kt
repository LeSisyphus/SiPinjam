package com.example.sipinjam2.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.R

data class BarangTersedia(
    val nama: String,
    val kategori: String,
    val imageUrl: String,
)

data class ItemDikembalikan(
    val nama: String,
    val lokasi: String,
    val tanggal: String,
    val icon: ImageVector,
)

@Composable
fun HomeUserScreen(
    onLihatSemuaBarang: () -> Unit = {},
    onKembalikan: (ItemDikembalikan) -> Unit = {},
    onBarangClick: (BarangTersedia) -> Unit = {},
    onBerandaClick: () -> Unit = {},
    onKatalogClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
) {
    val sipinjamBlue   = colorResource(R.color.sipinjam_blue)
    val backgroundGray = colorResource(R.color.background_gray)
    val cardWhite      = colorResource(R.color.card_white)
    val inputBg        = colorResource(R.color.input_bg)
    val textPrimary    = colorResource(R.color.text_primary)
    val textSecondary  = colorResource(R.color.text_secondary)
    val statusGreen    = colorResource(R.color.status_green)
    val statusGreenBg  = colorResource(R.color.status_green_bg)

    val daftarBarang = listOf(
        BarangTersedia("Sony Alpha A7III", "DIGITAL IMAGING", ""),
        BarangTersedia("Sony Alpha A7III", "DIGITAL IMAGING", ""),
    )

    val daftarDikembalikan = listOf(
        ItemDikembalikan("HDMI Cable 5m", "Lab Multimedia", "12 Mei", Icons.Filled.SwapVert),
        ItemDikembalikan("Tripod Excell", "Storage A", "08 Mei", Icons.Filled.ArrowUpward),
    )

    var selectedNav by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = backgroundGray,
        bottomBar = {
            BottomNavBar(
                selected = selectedNav,
                onBerandaClick = { selectedNav = 0; onBerandaClick() },
                onKatalogClick = { selectedNav = 1; onKatalogClick() },
                onRiwayatClick = { selectedNav = 2; onRiwayatClick() },
                onProfilClick  = { selectedNav = 3; onProfilClick() },
                cardWhite      = cardWhite,
                sipinjamBlue   = sipinjamBlue,
                textSecondary  = textSecondary,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    tint = sipinjamBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SiPinjam",
                    color = sipinjamBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardWhite)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = textSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Cari barang yang ingin kamu pinjam",
                    color = textSecondary.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Barang Tersedia",
                    color = textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "LIHAT SEMUA",
                    color = sipinjamBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.clickable { onLihatSemuaBarang() }
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(daftarBarang) { barang ->
                    BarangCard(
                        barang = barang,
                        cardWhite = cardWhite,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        statusGreen = statusGreen,
                        statusGreenBg = statusGreenBg,
                        onClick = { onBarangClick(barang) }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Perlu Dikembalikan",
                color = textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                daftarDikembalikan.forEach { item ->
                    KembalikanCard(
                        item = item,
                        cardWhite = cardWhite,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        sipinjamBlue = sipinjamBlue,
                        inputBg = inputBg,
                        onClick = { onKembalikan(item) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BarangCard(
    barang: BarangTersedia,
    cardWhite: Color,
    textPrimary: Color,
    textSecondary: Color,
    statusGreen: Color,
    statusGreenBg: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusGreenBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TERSEDIA",
                            color = statusGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = barang.nama,
                    color = textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = barang.kategori,
                    color = textSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun KembalikanCard(
    item: ItemDikembalikan,
    cardWhite: Color,
    textPrimary: Color,
    textSecondary: Color,
    sipinjamBlue: Color,
    inputBg: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardWhite)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(inputBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = sipinjamBlue,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nama,
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${item.lokasi} • ${item.tanggal}",
                color = textSecondary,
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(sipinjamBlue)
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = "Kembalikan",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    selected: Int,
    onBerandaClick: () -> Unit,
    onKatalogClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onProfilClick: () -> Unit,
    cardWhite: Color,
    sipinjamBlue: Color,
    textSecondary: Color,
) {
    NavigationBar(
        containerColor = cardWhite,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = onBerandaClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("BERANDA", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = sipinjamBlue,
                selectedTextColor = sipinjamBlue,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = cardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = onKatalogClick,
            icon = { Icon(Icons.Filled.Book, contentDescription = null) },
            label = { Text("KATALOG", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = sipinjamBlue,
                selectedTextColor = sipinjamBlue,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = cardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = onRiwayatClick,
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text("RIWAYAT", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = sipinjamBlue,
                selectedTextColor = sipinjamBlue,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = cardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 3,
            onClick = onProfilClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("PROFIL", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = sipinjamBlue,
                selectedTextColor = sipinjamBlue,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = cardWhite
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeUserScreenPreview() {
    MaterialTheme {
        HomeUserScreen()
    }
}