package com.example.sipinjam.screens.admin

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import com.example.sipinjam.data.model.BorrowingStatus
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.data.model.Barang
import com.example.sipinjam.data.model.Peminjaman
import com.example.sipinjam.data.model.User
import com.example.sipinjam.ui.components.SiPinjamTopBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.DividerColor
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.StatusBlue
import com.example.sipinjam.ui.theme.StatusBlueBg
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun DetailPengajuanScreen(
    peminjamanId: String,
    onBackClick: () -> Unit = {},
    onActionDone: () -> Unit = {},
    viewModel: DetailPengajuanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(peminjamanId) {
        viewModel.loadDetail(peminjamanId)
    }

    LaunchedEffect(uiState.actionDone) {
        if (uiState.actionDone) {
            onActionDone()
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            SiPinjamTopBar(
                title = stringResource(R.string.screen_approval_detail),
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.peminjaman != null) {
                DetailPengajuanBottomAction(
                    status = uiState.peminjaman?.status.orEmpty(),
                    isLoading = uiState.isActionLoading,
                    errorMessage = uiState.errorMessage,
                    onTolakClick = viewModel::tolakPengajuan,
                    onSetujuiClick = viewModel::setujuiPengajuan
                )
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SiPinjamBlue)
                }
            }

            uiState.peminjaman == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Data pengajuan tidak ditemukan",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                DetailPengajuanContent(
                    peminjaman = uiState.peminjaman!!,
                    barang = uiState.barang,
                    peminjam = uiState.peminjam,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DetailPengajuanContent(
    peminjaman: Peminjaman,
    barang: Barang?,
    peminjam: User?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(title = "Informasi Barang")
            InformasiBarangCard(peminjaman = peminjaman, barang = barang)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(title = "Detail Peminjaman")
            DetailPeminjamanCard(peminjaman = peminjaman, peminjam = peminjam)
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InformasiBarangCard(peminjaman: Peminjaman, barang: Barang?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (!barang?.fotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = barang?.fotoUrl,
                            contentDescription = peminjaman.namaBarang,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    KategoriChip(
                        kategori = barang?.kategori?.ifBlank { "-" } ?: "-"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = peminjaman.namaBarang.ifBlank { barang?.nama ?: "-" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Qty dipinjam: 1 unit",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }

            DetailInfoRow(
                label = "Kondisi Awal",
                value = barang?.kondisi?.ifBlank { "-" } ?: "-",
                valueColor = StatusGreen
            )

            DetailInfoRow(
                label = "Lokasi Pengambilan",
                value = barang?.lokasi?.ifBlank { "-" } ?: "-",
                valueColor = TextPrimary
            )
        }
    }
}

@Composable
private fun KategoriChip(kategori: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(StatusBlueBg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = kategori.uppercase(),
            color = StatusBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailPeminjamanCard(peminjaman: Peminjaman, peminjam: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            DetailInfoRow(
                label = "Peminjam",
                value = peminjam?.nama?.ifBlank { peminjaman.namaUser } ?: peminjaman.namaUser.ifBlank { "-" },
                valueColor = TextPrimary
            )
            DetailInfoRow(
                label = stringResource(R.string.label_tanggal_pinjam),
                value = peminjaman.tanggalPinjam.ifBlank { "-" },
                valueColor = TextPrimary
            )
            DetailInfoRow(
                label = stringResource(R.string.label_tanggal_kembali),
                value = peminjaman.tanggalKembali.ifBlank { "-" },
                valueColor = TextPrimary
            )
            DetailInfoRow(
                label = "Durasi",
                value = hitungDurasiHari(peminjaman.tanggalPinjam, peminjaman.tanggalKembali),
                valueColor = SiPinjamBlue
            )

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            Text(
                text = stringResource(R.string.label_alasan_peminjaman),
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = peminjaman.keperluan.ifBlank { "-" },
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 25.sp
            )
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailPengajuanBottomAction(
    status: String,
    isLoading: Boolean,
    errorMessage: String?,
    onTolakClick: () -> Unit,
    onSetujuiClick: () -> Unit
) {
    val canTakeAction = status.equals(BorrowingStatus.DIPROSES, ignoreCase = true)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = BackgroundGray
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = StatusRed,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            if (!canTakeAction) {
                Text(
                    text = "Pengajuan ini sudah berstatus $status.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onTolakClick,
                    enabled = canTakeAction && !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StatusRed,
                        disabledContentColor = TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_tolak),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSetujuiClick,
                    enabled = canTakeAction && !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SiPinjamBlue,
                        disabledContainerColor = SiPinjamBlue.copy(alpha = 0.45f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_setujui),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun hitungDurasiHari(tanggalPinjam: String, tanggalKembali: String): String {
    return try {
        val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val mulai = formatter.parse(tanggalPinjam)
        val selesai = formatter.parse(tanggalKembali)
        if (mulai == null || selesai == null) return "-"
        val diffMillis = selesai.time - mulai.time
        val hari = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        if (hari <= 0) "-" else "$hari Hari"
    } catch (e: Exception) {
        "-"
    }
}