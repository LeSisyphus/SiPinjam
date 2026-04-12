package com.example.sipinjam2.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.R

@Composable
fun PeminjamanScreen(
    namaBarang: String = "MacBook Pro M2 14-inch",
    kategoriBarang: String = "ELEKTRONIK",
    statusBarang: String = "TERSEDIA",
    onBackClick: () -> Unit = {},
    onKirimPermohonan: (tanggalPinjam: String, tanggalKembali: String, keperluan: String) -> Unit = { _, _, _ -> },
) {
    val sipinjamBlue   = colorResource(R.color.sipinjam_blue)
    val backgroundGray = colorResource(R.color.background_gray)
    val cardWhite      = colorResource(R.color.card_white)
    val inputBg        = colorResource(R.color.input_bg)
    val textPrimary    = colorResource(R.color.text_primary)
    val textSecondary  = colorResource(R.color.text_secondary)
    val statusGreen    = colorResource(R.color.status_green)
    val statusGreenBg  = colorResource(R.color.status_green_bg)

    var tanggalPinjam   by remember { mutableStateOf("12 Oct 2024") }
    var tanggalKembali  by remember { mutableStateOf("15 Oct 2024") }
    var keperluan       by remember { mutableStateOf("") }

    Scaffold(
        containerColor = backgroundGray,
        topBar = {
            Surface(
                color = cardWhite,
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
                            tint = textPrimary
                        )
                    }
                    Text(
                        text = "Ajukan Peminjaman",
                        color = textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onKirimPermohonan(tanggalPinjam, tanggalKembali, keperluan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue)
                    ) {
                        Text(
                            text = "Kirim Permohonan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Permohonan akan diproses oleh admin dalam 1×24 jam. Pastikan data yang anda masukkan sudah benar.",
                        color = textSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A2E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(sipinjamBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = kategoriBarang,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusGreenBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = statusBarang,
                                    color = statusGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                        Text(
                            text = namaBarang,
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tanggal Pinjam",
                    color = textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = tanggalPinjam,
                    onValueChange = { tanggalPinjam = it },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Pilih tanggal",
                            tint = sipinjamBlue
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = sipinjamBlue,
                        unfocusedContainerColor = cardWhite,
                        focusedContainerColor = cardWhite,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = textPrimary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tanggal Kembali",
                    color = textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = tanggalKembali,
                    onValueChange = { tanggalKembali = it },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Pilih tanggal",
                            tint = sipinjamBlue
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = sipinjamBlue,
                        unfocusedContainerColor = cardWhite,
                        focusedContainerColor = cardWhite,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = textPrimary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Keperluan/Alasan",
                    color = textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = keperluan,
                    onValueChange = { if (it.length <= 200) keperluan = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = {
                        Text(
                            text = "Tulis alasan peminjaman...",
                            color = textSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = sipinjamBlue,
                        unfocusedContainerColor = inputBg,
                        focusedContainerColor = inputBg,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = textPrimary
                    )
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${keperluan.length}/200",
                        color = textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PeminjamanScreenPreview() {
    MaterialTheme {
        PeminjamanScreen()
    }
}