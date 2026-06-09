package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sipinjam.ui.theme.*
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

data class DetailBarang(
    val id: String = "",
    val nama: String = "",
    val kategori: String = "",
    val totalUnit: Int = 0,
    val tersedia: Boolean = true,
    val kondisi: String = "",
    val jumlahTersedia: Int = 0,
    val lokasi: String = "",
    val maksimalPinjam: String = "",
    val deskripsi: String = "",
    val imageUrl: String = "",
)

@Composable
fun DetailBarangScreen(
    barangId: String,
    viewModel: DetailBarangViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onAjukanPeminjaman: (
        barangId: String,
        namaBarang: String,
        kategoriBarang: String,
        statusBarang: String
    ) -> Unit = { _, _, _, _ -> },
) {
    LaunchedEffect(barangId) {
        viewModel.loadBarangDetail(barangId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var deskripsiExpanded by rememberSaveable { mutableStateOf(true) }

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
                        text = stringResource(R.string.screen_item_detail),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        bottomBar = {
            uiState.barang?.let { barang ->
                val statusBarangRoute = if (barang.tersedia) "Tersedia" else "Tidak Tersedia"

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CardWhite,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            onAjukanPeminjaman(
                                barang.id,
                                barang.nama,
                                barang.kategori,
                                statusBarangRoute
                            )
                        },
                        enabled = barang.tersedia,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue)
                    ) {
                        Text(
                            text = if (barang.tersedia) {
                                stringResource(R.string.screen_borrow_item)
                            } else {
                                stringResource(R.string.status_tidak_tersedia)
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = SiPinjamBlue)
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "Terjadi kesalahan",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                uiState.barang?.let { barang ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
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
                                    tint = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SiPinjamBlue)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = barang.kategori,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.4.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(InputBg)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${barang.totalUnit} Unit",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (barang.tersedia) StatusGreenBg else StatusOrangeBg)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(if (barang.tersedia) StatusGreen else StatusOrange)
                                        )
                                        Text(
                                            text = if (barang.tersedia) "Tersedia" else "Dipinjam",
                                            color = if (barang.tersedia) StatusGreen else StatusOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Text(
                                text = barang.nama,
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 30.sp
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = InputBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.label_spesifikasi_detail),
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        SpekItem(
                                            label = "Kondisi",
                                            value = barang.kondisi,
                                            modifier = Modifier.weight(1f)
                                        )
                                        SpekItem(
                                            label = "Jumlah Tersedia",
                                            value = "${barang.jumlahTersedia} Unit",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        SpekItem(
                                            label = "Lokasi",
                                            value = barang.lokasi,
                                            modifier = Modifier.weight(1f)
                                        )
                                        SpekItem(
                                            label = "Maksimal Pinjam",
                                            value = barang.maksimalPinjam,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.label_deskripsi_barang),
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        TextButton(onClick = { deskripsiExpanded = !deskripsiExpanded }) {
                                            Text(
                                                text = if (deskripsiExpanded) "▲" else "▼",
                                                color = TextSecondary,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    if (deskripsiExpanded) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = barang.deskripsi,
                                            color = TextSecondary,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpekItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetailBarangScreenPreview() {
    MaterialTheme {
        DetailBarangScreen(barangId = "preview_barang")
    }
}