package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.data.model.Barang
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
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.StatusRedBg

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
                SearchSection()
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
                    text = stringResource(R.string.screen_home_return_needed),
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
            text = stringResource(R.string.app_name),
            color = SiPinjamBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SearchSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.screen_home_search_prompt),
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
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
                        text = stringResource(R.string.status_tersedia_upper),
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
                text = stringResource(R.string.btn_kembalikan),
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
            .background(StatusRedBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = StatusRed,
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