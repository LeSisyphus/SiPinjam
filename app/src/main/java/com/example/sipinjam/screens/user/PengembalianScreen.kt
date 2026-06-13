package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.ui.components.SiPinjamTopBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.InfoOrangeBg
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusOrange
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary
import com.example.sipinjam.di.rememberSiPinjamViewModelFactory

@Composable
fun PengembalianScreen(
    peminjamanId: String = "",
    barangId: String = "",
    userId: String = "",
    namaBarang: String = "MacBook Pro M2",
    tanggalPinjam: String = "12 Mei",
    tanggalJatuhTempo: String = "14 Mei",
    onBackClick: () -> Unit = {},
    onKirimPengembalian: () -> Unit = {},
    viewModel: PengembalianViewModel = viewModel(factory = rememberSiPinjamViewModelFactory())
) {
    val context = LocalContext.current

    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var catatan by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val barang by viewModel.barang.collectAsState()
    val isBarangLoading by viewModel.isBarangLoading.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sukses by viewModel.sukses.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val catatanAdmin by viewModel.catatanAdmin.collectAsState()

    val namaBarangTampil = barang?.nama
        ?.takeIf { it.isNotBlank() }
        ?: namaBarang

    val fotoBarangUrl = barang?.fotoUrl.orEmpty()

    LaunchedEffect(sukses) {
        if (sukses) {
            onKirimPengembalian()
            viewModel.resetState()
        }
    }

    LaunchedEffect(barangId) {
        if (barangId.isNotBlank()) {
            viewModel.muatBarang(barangId)
        }
    }

    LaunchedEffect(peminjamanId) {
        if (peminjamanId.isNotBlank()) {
            viewModel.muatCatatanAdmin(peminjamanId)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            fotoUri = uri
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.screen_return_confirm_title),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.screen_return_confirm_subtitle),
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        fotoUri?.let { uri ->
                            viewModel.kirimPengembalian(
                                context = context,
                                peminjamanId = peminjamanId,
                                barangId = barangId,
                                userId = userId,
                                fotoUri = uri,
                                catatan = catatan
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.btn_ya_kembalikan), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.btn_batal), color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = CardWhite
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            SiPinjamTopBar(
                title = stringResource(R.string.screen_return_item),
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = StatusRed,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = { showDialog = true },
                        enabled = fotoUri != null && !isLoading,
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
                                text = stringResource(R.string.screen_return_confirmation_section),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
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
                    BarangImage(
                        imageUrl = fotoBarangUrl,
                        isLoading = isBarangLoading,
                        modifier = Modifier.size(56.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = namaBarangTampil,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column {
                                Text(
                                    text = stringResource(R.string.label_pinjam),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = tanggalPinjam,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column {
                                Text(
                                    text = stringResource(R.string.label_jatuh_tempo),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = tanggalJatuhTempo,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.label_foto_kondisi_barang),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (fotoUri == null) CardWhite else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = SiPinjamBlue.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoUri == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(SiPinjamBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                    contentDescription = null,
                                    tint = SiPinjamBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = stringResource(R.string.label_foto_kondisi_barang),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = stringResource(R.string.hint_foto_kondisi),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Foto kondisi barang",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SiPinjamBlue)
                                .clickable { galleryLauncher.launch("image/*") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.btn_ganti_foto),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.label_catatan_kondisi),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = catatan,
                    onValueChange = {
                        if (it.length <= 200) {
                            catatan = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.hint_catatan_kondisi),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(InfoOrangeBg)
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = StatusOrange,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.msg_return_photo_reminder),
                    color = StatusOrange,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            if (!catatanAdmin.isNullOrBlank()) {
                Text(
                    text = "Catatan Admin: $catatanAdmin",
                    color = StatusRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BarangImage(
    imageUrl: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkImageBg),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = SiPinjamBlue,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            imageUrl.isNotBlank() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto barang",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = SiPinjamBlue,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PengembalianScreenPreview() {
    MaterialTheme {
        PengembalianScreen()
    }
}
