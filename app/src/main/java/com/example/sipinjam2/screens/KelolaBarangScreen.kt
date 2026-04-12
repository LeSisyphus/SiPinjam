package com.example.sipinjam2.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.R

data class BarangAdmin(
    val id: String,
    val nama: String,
    val kategori: String,
    val stok: Int,
    val tersedia: Boolean,
    val imageUrl: String = "",
)

@Composable
fun KelolaBarangScreen(
    daftarBarang: List<BarangAdmin> = listOf(
        BarangAdmin("1", "Camera DSLR Canon", "ELEKTRONIK", 2, true),
        BarangAdmin("2", "Proyektor Epson", "ELEKTRONIK", 1, true),
        BarangAdmin("3", "Tripod Kamera", "OPTIK", 3, true),
        BarangAdmin("4", "Kabel HDMI 5m", "KABEL", 0, false),
    ),
    onTambahClick: () -> Unit = {},
    onEditClick: (BarangAdmin) -> Unit = {},
    onDeleteClick: (BarangAdmin) -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
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
    val statusRed      = colorResource(R.color.status_red)
    val statusRedBg    = colorResource(R.color.status_red_bg)

    val kategoriList = listOf("Semua", "Elektronik", "Optik", "Kabel")
    var selectedKategori by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredBarang = daftarBarang.filter { barang ->
        val matchKategori = selectedKategori == "Semua" ||
                barang.kategori.equals(selectedKategori, ignoreCase = true)
        val matchSearch = barang.nama.contains(searchQuery, ignoreCase = true) ||
                barang.id.contains(searchQuery, ignoreCase = true)
        matchKategori && matchSearch
    }

    var selectedNav by remember { mutableStateOf(1) }

    Scaffold(
        containerColor = backgroundGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTambahClick,
                containerColor = sipinjamBlue,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Tambah",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        bottomBar = {
            AdminBottomNavBar(
                selected = selectedNav,
                onDashboardClick  = { selectedNav = 0; onDashboardClick() },
                onBarangClick     = { selectedNav = 1; onBarangClick() },
                onPermintaanClick = { selectedNav = 2; onPermintaanClick() },
                onProfilClick     = { selectedNav = 3; onProfilClick() },
                cardWhite         = cardWhite,
                sipinjamBlue      = sipinjamBlue,
                textSecondary     = textSecondary,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))

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
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = textPrimary
                    ),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Cari nama barang atau ID...",
                                color = textSecondary.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KATEGORI",
                    color = textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(sipinjamBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${daftarBarang.size} barang terdaftar",
                        color = sipinjamBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(kategoriList) { kategori ->
                    val isSelected = kategori == selectedKategori
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) sipinjamBlue else cardWhite)
                            .clickable { selectedKategori = kategori }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = kategori,
                            color = if (isSelected) Color.White else textSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredBarang.forEach { barang ->
                    BarangAdminCard(
                        barang = barang,
                        cardWhite = cardWhite,
                        inputBg = inputBg,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        sipinjamBlue = sipinjamBlue,
                        statusGreen = statusGreen,
                        statusGreenBg = statusGreenBg,
                        statusRed = statusRed,
                        statusRedBg = statusRedBg,
                        onEditClick = { onEditClick(barang) },
                        onDeleteClick = { onDeleteClick(barang) }
                    )
                }
            }

            Spacer(Modifier.height(88.dp))
        }
    }
}

@Composable
private fun BarangAdminCard(
    barang: BarangAdmin,
    cardWhite: Color,
    inputBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    sipinjamBlue: Color,
    statusGreen: Color,
    statusGreenBg: Color,
    statusRed: Color,
    statusRedBg: Color,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = barang.nama,
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(sipinjamBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = barang.kategori,
                        color = sipinjamBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Stok: ${barang.stok} unit",
                    color = textSecondary,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (barang.tersedia) statusGreenBg else statusRedBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (barang.tersedia) "Tersedia" else "Habis",
                    color = if (barang.tersedia) statusGreen else statusRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(sipinjamBlue.copy(alpha = 0.1f))
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = sipinjamBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusRedBg)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Hapus",
                    tint = statusRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    textStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit,
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        textStyle = textStyle,
        decorationBox = decorationBox
    )
}

@Composable
private fun AdminBottomNavBar(
    selected: Int,
    onDashboardClick: () -> Unit,
    onBarangClick: () -> Unit,
    onPermintaanClick: () -> Unit,
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
            onClick = onDashboardClick,
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            label = { Text("DASHBOARD", fontSize = 10.sp) },
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
            onClick = onBarangClick,
            icon = { Icon(Icons.Filled.Inventory, contentDescription = null) },
            label = { Text("BARANG", fontSize = 10.sp) },
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
            onClick = onPermintaanClick,
            icon = { Icon(Icons.Filled.RequestPage, contentDescription = null) },
            label = { Text("PERMINTAAN", fontSize = 10.sp) },
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
fun KelolaBarangScreenPreview() {
    MaterialTheme {
        KelolaBarangScreen()
    }
}