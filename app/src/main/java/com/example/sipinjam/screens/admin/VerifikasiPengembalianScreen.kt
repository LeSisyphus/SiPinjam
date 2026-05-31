package com.example.sipinjam.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary

private val SoftBlueBg = Color(0xFFDCEBFF)
private val SoftItemBg = Color(0xFFF0F2FB)
private val NoteBg = Color(0xFFF6F8FF)

private val RoleBlueBg = Color(0xFFD8E9FF)
private val RoleBlueText = Color(0xFF4F74A6)
private val RoleCyanBg = Color(0xFFD8F0FF)
private val RoleCyanText = Color(0xFF156D9E)
private val RoleOrangeBg = Color(0xFFFFE5C2)
private val RoleOrangeText = Color(0xFFA76012)

@Composable
fun VerifikasiPengembalianScreen(
    pengembalianId: String = "",
    onBackClick: () -> Unit = {},
    onVerifikasiDone: () -> Unit = {},
    viewModel: VerifikasiPengembalianViewModel = viewModel()
) {
    val pengembalian by viewModel.pengembalian.collectAsState()
    val detailUiState by viewModel.detailUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sukses by viewModel.sukses.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val catatanTolak by viewModel.catatanTolak.collectAsState()

    var kondisi by remember { mutableStateOf("Baik") }
    var catatanVerifikasi by remember { mutableStateOf("") }
    val kondisiList = listOf("Baik", "Rusak Ringan", "Rusak Berat")

    LaunchedEffect(pengembalianId) {
        if (pengembalianId.isNotBlank()) {
            viewModel.muatPengembalian(pengembalianId)
        }
    }

    LaunchedEffect(sukses) {
        if (sukses) {
            onVerifikasiDone()
            viewModel.resetState()
        }
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
                        text = "Verifikasi Pengembalian",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = StatusRed,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.verifikasi(
                                pengembalianId = pengembalianId,
                                catatan = catatanVerifikasi,
                                kondisi = kondisi
                            )
                        },
                        enabled = !isLoading && pengembalian != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Verifikasi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.tolak(
                                pengembalianId = pengembalianId,
                                catatan = catatanVerifikasi
                            )
                        },
                        enabled = !isLoading && pengembalian != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        border = BorderStroke(1.dp, StatusRed)
                    ) {
                        Text(
                            text = "Tolak Pengembalian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusRed
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading && pengembalian == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SiPinjamBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                HeaderPengembalianCard(
                    namaPeminjam = detailUiState.namaPeminjam,
                    rolePeminjam = detailUiState.rolePeminjam,
                    fotoPeminjamUrl = detailUiState.fotoPeminjamUrl,
                    namaBarang = detailUiState.namaBarang,
                    fotoBarangUrl = detailUiState.fotoBarangUrl,
                    tanggalKembali = pengembalian?.tanggalKembali ?: "-"
                )

                if (!pengembalian?.fotoKondisiUrl.isNullOrBlank()) {
                    FotoKondisiCard(
                        imageUrl = pengembalian?.fotoKondisiUrl.orEmpty()
                    )
                }

                CatatanPeminjamCard(
                    catatan = detailUiState.catatanPeminjam
                )

                KondisiBarangCard(
                    kondisiList = kondisiList,
                    kondisi = kondisi,
                    onKondisiChange = { kondisi = it }
                )

                CatatanVerifikasiCard(
                    value = catatanVerifikasi,
                    onValueChange = { catatanVerifikasi = it }
                )

                if (!catatanTolak.isNullOrBlank()) {
                    Text(
                        text = "Catatan Admin: $catatanTolak",
                        color = StatusRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderPengembalianCard(
    namaPeminjam: String,
    rolePeminjam: String,
    fotoPeminjamUrl: String,
    namaBarang: String,
    fotoBarangUrl: String,
    tanggalKembali: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                UserImage(
                    imageUrl = fotoPeminjamUrl,
                    modifier = Modifier.size(46.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = namaPeminjam,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    RoleBadge(
                        role = rolePeminjam,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }

                Text(
                    text = tanggalKembali,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftItemBg)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BarangImage(
                    imageUrl = fotoBarangUrl,
                    modifier = Modifier.size(46.dp)
                )

                Text(
                    text = namaBarang,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FotoKondisiCard(
    imageUrl: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto Kondisi Barang",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Foto Kondisi Pengembalian",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CatatanPeminjamCard(
    catatan: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "CATATAN PENGEMBALIAN PEMINJAM",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NoteBg)
                    .padding(14.dp)
            ) {
                Text(
                    text = catatan.ifBlank { "Peminjam tidak menambahkan catatan pengembalian." },
                    color = if (catatan.isBlank()) {
                        TextSecondary.copy(alpha = 0.7f)
                    } else {
                        TextPrimary
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = if (catatan.isBlank()) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun KondisiBarangCard(
    kondisiList: List<String>,
    kondisi: String,
    onKondisiChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "KONDISI BARANG",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                kondisiList.forEach { item ->
                    val isSelected = kondisi == item

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SiPinjamBlue else InputBg)
                            .clickable { onKondisiChange(item) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatatanVerifikasiCard(
    value: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CATATAN VERIFIKASI ADMIN",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "Tambahkan catatan jika ada kendala...",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = SiPinjamBlue,
                    unfocusedContainerColor = InputBg,
                    focusedContainerColor = InputBg
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            )
        }
    }
}

@Composable
private fun UserImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SoftBlueBg),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto Peminjam",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = SiPinjamBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BarangImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkImageBg),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto Barang",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = null,
                tint = SiPinjamBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RoleBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val normalizedRole = role.uppercase()

    val badgeColor = when (normalizedRole) {
        "DOSEN" -> RoleCyanBg
        "STAF" -> RoleOrangeBg
        "STAFF" -> RoleOrangeBg
        else -> RoleBlueBg
    }

    val textColor = when (normalizedRole) {
        "DOSEN" -> RoleCyanText
        "STAF" -> RoleOrangeText
        "STAFF" -> RoleOrangeText
        else -> RoleBlueText
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(badgeColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = normalizedRole,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun VerifikasiPengembalianScreenPreview() {
    MaterialTheme {
        VerifikasiPengembalianScreen()
    }
}