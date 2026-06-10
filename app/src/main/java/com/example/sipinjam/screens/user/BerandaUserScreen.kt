package com.example.sipinjam.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.data.model.Barang
import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.model.HolidayStatus
import com.example.sipinjam.ui.components.UserBottomNavBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusGreenBg
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary

data class BarangTersedia(
    val id: String,
    val nama: String,
    val kategori: String,
    val imageUrl: String,
)

data class ItemDikembalikan(
    val peminjamanId: String,
    val barangId: String,
    val userId: String,
    val nama: String,
    val lokasi: String,
    val tanggalPinjam: String,
    val tanggalJatuhTempo: String,
    val imageUrl: String = "",
    val icon: ImageVector = Icons.Filled.SwapVert,
)

@Composable
fun BerandaUserScreen(
    viewModel: BerandaUserViewModel = viewModel(),
    onLihatSemuaBarang: () -> Unit = {},
    onSearchBarang: (String) -> Unit = {},
    onBarangClick: (Barang) -> Unit = {},
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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedNav by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            UserBottomNavBar(
                selected = selectedNav,
                onBerandaClick = onBerandaClick,
                onKatalogClick = onKatalogClick,
                onRiwayatClick = onRiwayatClick,
                onProfilClick = onProfilClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HeaderSection()
            }

            item {
                SearchSection(
                    onSearchSubmit = onSearchBarang
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                HolidayInfoCard(
                    todayStatus = uiState.todayHolidayStatus,
                    monthlyHolidays = uiState.monthlyHolidays,
                    isLoading = uiState.isHolidayLoading,
                    errorMessage = uiState.holidayErrorMessage,
                    onRefreshClick = viewModel::refreshHolidayInfo,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                SectionHeader(
                    title = "Barang Tersedia",
                    actionText = "LIHAT SEMUA",
                    onActionClick = onLihatSemuaBarang
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
            }

            item {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = SiPinjamBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    uiState.barangTersedia.isEmpty() -> {
                        EmptyStateCard(
                            text = "Belum ada barang tersedia.",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    else -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.barangTersedia,
                                key = { barang -> barang.id }
                            ) { barang ->
                                BarangCard(
                                    barang = barang,
                                    onClick = {
                                        onBarangClick(
                                            Barang(
                                                id = barang.id,
                                                nama = barang.nama,
                                                kategori = barang.kategori
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(28.dp))
            }

            item {
                Text(
                    text = "Perlu Dikembalikan",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
            }

            if (uiState.itemDikembalikan.isEmpty()) {
                item {
                    EmptyStateCard(
                        text = "Tidak ada barang yang perlu dikembalikan.",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else {
                items(
                    items = uiState.itemDikembalikan,
                    key = { item -> item.peminjamanId }
                ) { item ->
                    KembalikanCard(
                        item = item,
                        onClick = {
                            onPengembalianClick(
                                item.peminjamanId,
                                item.barangId,
                                item.userId,
                                item.nama,
                                item.tanggalPinjam,
                                item.tanggalJatuhTempo
                            )
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                item {
                    Spacer(Modifier.height(12.dp))
                    ErrorCard(
                        message = message,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Book,
            contentDescription = null,
            tint = SiPinjamBlue,
            modifier = Modifier.size(26.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "SiPinjam",
            color = SiPinjamBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SearchSection(
    onSearchSubmit: (String) -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    fun submitSearch() {
        focusManager.clearFocus()
        onSearchSubmit(query.trim())
    }

    BasicTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            color = TextPrimary
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { submitSearch() }
        ),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { submitSearch() }
                )
                Spacer(Modifier.width(10.dp))
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari barang yang ingin kamu pinjam",
                            color = TextSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun HolidayInfoCard(
    todayStatus: HolidayStatus?,
    monthlyHolidays: List<Holiday>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val referenceDate = todayStatus?.date.orEmpty()
    val nearestHoliday = monthlyHolidays.firstOrNull { holiday ->
        referenceDate.isBlank() || holiday.date >= referenceDate
    } ?: monthlyHolidays.firstOrNull()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Info Hari Libur",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Data dari API pihak ketiga + cache Room",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "REFRESH",
                    color = SiPinjamBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onRefreshClick() }
                )
            }

            Spacer(Modifier.height(12.dp))

            when {
                isLoading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = SiPinjamBlue,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Memuat info hari libur...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                errorMessage != null && todayStatus == null && monthlyHolidays.isEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp
                    )
                }

                else -> {
                    val todayText = if (todayStatus?.isHoliday == true) {
                        "Hari ini libur: ${todayStatus.displayName}"
                    } else {
                        "Hari ini bukan hari libur nasional/cuti bersama."
                    }

                    Text(
                        text = todayText,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = nearestHoliday?.let { holiday ->
                            "Libur terdekat bulan ini: ${holiday.date} • ${holiday.name}"
                        } ?: "Belum ada data libur pada bulan ini.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    if (errorMessage != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Menampilkan cache terakhir karena refresh gagal.",
                            color = Color(0xFFF57C00),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = SiPinjamBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
private fun BarangCard(
    barang: BarangTersedia,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(DarkImageBg),
                contentAlignment = Alignment.Center
            ) {
                if (barang.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = barang.imageUrl,
                        contentDescription = barang.nama,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(StatusGreenBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "TERSEDIA",
                        color = StatusGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = barang.nama,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = barang.kategori,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun KembalikanCard(
    item: ItemDikembalikan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(InputBg),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.nama,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = SiPinjamBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nama,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Text(
                text = "${item.lokasi} • Jatuh tempo: ${item.tanggalJatuhTempo}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SiPinjamBlue)
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
private fun EmptyStateCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFEBEE))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = Color(0xFFD32F2F),
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BerandaUserScreenPreview() {
    MaterialTheme {
        BerandaUserScreen()
    }
}