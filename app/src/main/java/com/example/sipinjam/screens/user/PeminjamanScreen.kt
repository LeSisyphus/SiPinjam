package com.example.sipinjam.screens.user

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusGreenBg
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.StatusRedBg
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeminjamanScreen(
    barangId: String = "",
    namaBarang: String = "MacBook Pro M2 14-inch",
    kategoriBarang: String = "ELEKTRONIK",
    statusBarang: String = "TERSEDIA",
    onBackClick: () -> Unit = {},
    onKirimPermohonan: (tanggalPinjam: String, tanggalKembali: String, keperluan: String) -> Unit = { _, _, _ -> },
    viewModel: PeminjamanViewModel = viewModel()
) {
    var tanggalPinjam by rememberSaveable { mutableStateOf("") }
    var tanggalKembali by rememberSaveable { mutableStateOf("") }
    var keperluan by rememberSaveable { mutableStateOf("") }

    val barang by viewModel.barang.collectAsState()
    val isBarangLoading by viewModel.isBarangLoading.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sukses by viewModel.sukses.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDatePickerPinjam by remember { mutableStateOf(false) }
    var showDatePickerKembali by remember { mutableStateOf(false) }

    val datePickerStatePinjam = rememberDatePickerState()
    val datePickerStateKembali = rememberDatePickerState()

    val isBarangAvailable = barang?.let {
        it.stok > 0 && it.tersedia
    } ?: statusBarang.equals("TERSEDIA", ignoreCase = true)

    val statusLabel = when {
        isBarangLoading -> "MEMUAT"
        barang == null -> statusBarang.uppercase()
        isBarangAvailable -> "TERSEDIA"
        else -> "TIDAK TERSEDIA"
    }

    val canSubmit = !isLoading &&
            !isBarangLoading &&
            tanggalPinjam.isNotBlank() &&
            tanggalKembali.isNotBlank() &&
            keperluan.isNotBlank() &&
            barang != null &&
            (barang?.stok ?: 0) > 0 &&
            (barang?.tersedia ?: false)

    LaunchedEffect(barangId) {
        viewModel.loadBarang(barangId)
    }

    fun formatTanggal(millis: Long?): String {
        if (millis == null) return ""
        val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        return formatter.format(Date(millis))
    }

    if (showDatePickerPinjam) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerPinjam = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = formatTanggal(datePickerStatePinjam.selectedDateMillis)
                        if (selectedDate.isNotBlank()) {
                            tanggalPinjam = selectedDate
                        }
                        showDatePickerPinjam = false
                    }
                ) {
                    Text(
                        text = "OK",
                        color = SiPinjamBlue
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerPinjam = false
                    }
                ) {
                    Text(
                        text = "Batal",
                        color = TextSecondary
                    )
                }
            }
        ) {
            DatePicker(state = datePickerStatePinjam)
        }
    }

    if (showDatePickerKembali) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerKembali = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = formatTanggal(datePickerStateKembali.selectedDateMillis)
                        if (selectedDate.isNotBlank()) {
                            tanggalKembali = selectedDate
                        }
                        showDatePickerKembali = false
                    }
                ) {
                    Text(
                        text = "OK",
                        color = SiPinjamBlue
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerKembali = false
                    }
                ) {
                    Text(
                        text = "Batal",
                        color = TextSecondary
                    )
                }
            }
        ) {
            DatePicker(state = datePickerStateKembali)
        }
    }

    if (sukses) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SiPinjamBlue,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Permohonan Terkirim!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Permohonan peminjaman kamu sudah berhasil dikirim. Admin akan memproses dalam 1×24 jam.",
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onKirimPermohonan(tanggalPinjam, tanggalKembali, keperluan)
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kembali ke Beranda",
                        color = Color.White
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = CardWhite
        )
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
                        text = "Ajukan Peminjaman",
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = StatusRed,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.kirimPermohonan(
                                barangId = barangId,
                                namaBarang = namaBarang,
                                tanggalPinjam = tanggalPinjam,
                                tanggalKembali = tanggalKembali,
                                keperluan = keperluan
                            )
                        },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SiPinjamBlue,
                            disabledContainerColor = SiPinjamBlue.copy(alpha = 0.45f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Kirim Permohonan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Permohonan akan diproses oleh admin dalam 1×24 jam. Pastikan data yang anda masukkan sudah benar.",
                        color = TextSecondary,
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
                colors = CardDefaults.cardColors(containerColor = CardWhite),
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
                            .background(DarkImageBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!barang?.fotoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = barang?.fotoUrl,
                                contentDescription = barang?.nama ?: namaBarang,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Book,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SiPinjamBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = barang?.kategori
                                        ?.uppercase()
                                        ?.ifBlank { kategoriBarang }
                                        ?: kategoriBarang,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isBarangAvailable) {
                                            StatusGreenBg
                                        } else {
                                            StatusRedBg
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = statusLabel,
                                    color = if (isBarangAvailable) StatusGreen else StatusRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }

                        Text(
                            text = barang?.nama?.ifBlank { namaBarang } ?: namaBarang,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )

                        Text(
                            text = "Stok: ${barang?.stok ?: "-"} • Maks. pinjam: ${
                                formatMaksimalPinjamLabel(barang?.maksimalPinjam)
                            }",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (barang != null && !isBarangAvailable) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusRedBg)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Barang ini sedang tidak tersedia atau stok sudah habis, sehingga tidak dapat diajukan untuk peminjaman.",
                        color = StatusRed,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Tanggal Pinjam",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = tanggalPinjam,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "cth: 1 Juni 2026",
                            color = TextSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePickerPinjam = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Pilih tanggal pinjam",
                                tint = SiPinjamBlue
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = SiPinjamBlue,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Tanggal Kembali",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = tanggalKembali,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "cth: 5 Juni 2026",
                            color = TextSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePickerKembali = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Pilih tanggal kembali",
                                tint = SiPinjamBlue
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = SiPinjamBlue,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Keperluan/Alasan",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = keperluan,
                    onValueChange = {
                        if (it.length <= 200) {
                            keperluan = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = {
                        Text(
                            text = "Tulis alasan peminjaman...",
                            color = TextSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = SiPinjamBlue,
                        unfocusedContainerColor = InputBg,
                        focusedContainerColor = InputBg,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${keperluan.length}/200",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun formatMaksimalPinjamLabel(value: String?): String {
    if (value.isNullOrBlank()) return "-"

    val angka = value.filter { it.isDigit() }

    return if (angka.isBlank()) {
        value
    } else {
        "$angka hari"
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PeminjamanScreenPreview() {
    MaterialTheme {
        PeminjamanScreen()
    }
}