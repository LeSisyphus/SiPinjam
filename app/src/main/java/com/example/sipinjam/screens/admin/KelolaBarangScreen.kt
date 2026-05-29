package com.example.sipinjam.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.sipinjam.R

data class BarangAdmin(
    val id: String,
    val nama: String,
    val kategori: String,
    val stok: Int,
    val tersedia: Boolean,
    val imageUrl: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaBarangScreen(
    daftarBarang: List<BarangAdmin> = emptyList(),
    showEditDialog: Boolean = false,
    barangToEdit: BarangAdmin? = null,
    showDeleteDialog: Boolean = false,
    barangToDelete: BarangAdmin? = null,
    isLoading: Boolean = false,
    isSuccess: Boolean = false,
    onTambahConfirm: (nama: String, kategori: String, stok: Int, kondisi: String, lokasi: String, maksPinjam: String, deskripsi: String, imageUri: Uri?) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onEditClick: (BarangAdmin) -> Unit = {},
    onEditConfirm: (id: String, nama: String, kategori: String, stok: Int, imageUri: Uri?) -> Unit = { _, _, _, _, _ -> },
    onEditDismiss: () -> Unit = {},
    onDeleteClick: (BarangAdmin) -> Unit = {},
    onDeleteConfirm: () -> Unit = {},
    onDeleteDismiss: () -> Unit = {},
    onSuccessDismiss: () -> Unit = {},
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

    var showTambahDialog by remember { mutableStateOf(false) }
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }

    var inputNama by remember { mutableStateOf("") }
    var inputKategori by remember { mutableStateOf("Elektronik") }
    var inputStok by remember { mutableStateOf("") }
    var inputKondisi by remember { mutableStateOf("") }
    var inputLokasi by remember { mutableStateOf("") }
    var inputMaksimalPinjam by remember { mutableStateOf("") }
    var inputDeskripsi by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    var editNama by remember(barangToEdit) { mutableStateOf(barangToEdit?.nama ?: "") }
    var editKategori by remember(barangToEdit) { mutableStateOf(barangToEdit?.kategori ?: "") }
    var editStok by remember(barangToEdit) { mutableStateOf(barangToEdit?.stok?.toString() ?: "") }

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
                onClick = { showTambahDialog = true },
                containerColor = sipinjamBlue,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White)
                    Text(text = "Tambah", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = textPrimary),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(text = "Cari nama barang atau ID...", color = textSecondary.copy(alpha = 0.6f), fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "KATEGORI", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(sipinjamBlue.copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(text = "${daftarBarang.size} barang terdaftar", color = sipinjamBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                        Text(text = kategori, color = if (isSelected) Color.White else textSecondary, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (isLoading && !showTambahDialog && !showEditDialog) {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = sipinjamBlue)
                }
            }

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

    // tambah barang
    if (showTambahDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showTambahDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            containerColor = cardWhite,
            confirmButton = {
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Tambah Barang Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Foto Barang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                        letterSpacing = 0.4.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(inputBg)
                            .border(1.5.dp, textSecondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .clickable { if (!isLoading) launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Preview Gambar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = null,
                                    tint = sipinjamBlue.copy(alpha = 0.7f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Pilih Foto dari Galeri",
                                    color = textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = sipinjamBlue,
                        focusedLabelColor = sipinjamBlue,
                        unfocusedBorderColor = textSecondary.copy(alpha = 0.2f),
                        unfocusedLabelColor = textSecondary,
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        disabledContainerColor = inputBg
                    )

                    OutlinedTextField(value = inputNama, onValueChange = { inputNama = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputKategori, onValueChange = { inputKategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputStok, onValueChange = { inputStok = it }, label = { Text("Jumlah Stok") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputKondisi, onValueChange = { inputKondisi = it }, label = { Text("Kondisi Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputLokasi, onValueChange = { inputLokasi = it }, label = { Text("Lokasi Penyimpanan") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputMaksimalPinjam, onValueChange = { inputMaksimalPinjam = it }, label = { Text("Maksimal Hari Pinjam") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = inputDeskripsi, onValueChange = { inputDeskripsi = it }, label = { Text("Deskripsi Lengkap") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, minLines = 3, enabled = !isLoading)

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showTambahDialog = false },
                            enabled = !isLoading,
                            border = androidx.compose.foundation.BorderStroke(1.dp, textSecondary.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal", color = textSecondary, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue),
                            enabled = !isLoading && inputNama.isNotBlank() && inputStok.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onTambahConfirm(
                                    inputNama, inputKategori, inputStok.toIntOrNull() ?: 0,
                                    inputKondisi, inputLokasi, inputMaksimalPinjam, inputDeskripsi,
                                    selectedImageUri
                                )
                                inputNama = ""; inputStok = ""; inputKondisi = ""; inputLokasi = ""; inputMaksimalPinjam = ""; inputDeskripsi = ""
                                selectedImageUri = null
                                showTambahDialog = false
                            }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        )
    }

    // pop up sukses tambah barang
    if (isSuccess) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = sipinjamBlue, modifier = Modifier.size(48.dp)) },
            title = { Text(text = "Barang Ditambahkan!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = textPrimary) },
            text = { Text(text = "Data inventaris barang baru telah berhasil disimpan dan diunggah ke dalam sistem database.", textAlign = TextAlign.Center, color = textSecondary, fontSize = 14.sp, lineHeight = 20.sp) },
            confirmButton = {
                Button(
                    onClick = onSuccessDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Selesai", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = cardWhite
        )
    }

    // edit barang
    if (showEditDialog && barangToEdit != null) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) onEditDismiss() },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            containerColor = cardWhite,
            confirmButton = {
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(text = "Edit Data Barang", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(sipinjamBlue.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("ID Barang: ${barangToEdit.id}", color = sipinjamBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Text(text = "Foto Barang (Klik untuk ganti)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textSecondary, letterSpacing = 0.4.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(inputBg)
                            .border(1.5.dp, textSecondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .clickable { if (!isLoading) launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(painter = rememberAsyncImagePainter(selectedImageUri), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else if (barangToEdit.imageUrl.isNotEmpty()) {
                            Image(painter = rememberAsyncImagePainter(barangToEdit.imageUrl), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Filled.Image, contentDescription = null, tint = sipinjamBlue.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
                                Spacer(Modifier.height(6.dp))
                                Text(text = "Pilih Foto dari Galeri", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = sipinjamBlue,
                        focusedLabelColor = sipinjamBlue,
                        unfocusedBorderColor = textSecondary.copy(alpha = 0.2f),
                        unfocusedLabelColor = textSecondary,
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        disabledContainerColor = inputBg
                    )

                    OutlinedTextField(value = editNama, onValueChange = { editNama = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = editKategori, onValueChange = { editKategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)
                    OutlinedTextField(value = editStok, onValueChange = { editStok = it }, label = { Text("Jumlah Stok") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = textFieldColors, enabled = !isLoading, singleLine = true)

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedImageUri = null; onEditDismiss() },
                            enabled = !isLoading,
                            border = androidx.compose.foundation.BorderStroke(1.dp, textSecondary.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal", color = textSecondary, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue),
                            enabled = !isLoading && editNama.isNotBlank() && editStok.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (editNama.isNotBlank() && editStok.isNotBlank()) {
                                    onEditConfirm(barangToEdit.id, editNama, editKategori, editStok.toIntOrNull() ?: 0, selectedImageUri)
                                    selectedImageUri = null
                                }
                            }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Simpan Perubahan", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        )
    }

    // hapus barang
    if (showDeleteDialog && barangToDelete != null) {
        AlertDialog(
            onDismissRequest = onDeleteDismiss,
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            containerColor = cardWhite,
            confirmButton = {
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Hapus Barang?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Apakah anda yakin ingin menghapus barang \"${barangToDelete.nama}\" dari daftar inventaris aplikasi?",
                        textAlign = TextAlign.Center,
                        color = textSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDeleteDismiss,
                            border = androidx.compose.foundation.BorderStroke(1.dp, textSecondary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal", color = textSecondary, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                onDeleteConfirm()
                                showDeleteSuccessDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = statusRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }


    // pop up sukses hapus barang
    if (showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSuccessDialog = false },
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = statusRed, modifier = Modifier.size(48.dp)) },
            title = { Text(text = "Barang Berhasil Dihapus", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = textPrimary) },
            text = { Text(text = "Data barang tersebut telah sepenuhnya dihapus dan diperbarui dari database sistem SiPinjam.", textAlign = TextAlign.Center, color = textSecondary, fontSize = 14.sp, lineHeight = 20.sp) },
            confirmButton = {
                Button(
                    onClick = { showDeleteSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selesai", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = cardWhite
        )
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
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(cardWhite).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            if (barang.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(barang.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = Icons.Filled.Book, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(28.dp))
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = barang.nama, color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(sipinjamBlue.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(text = barang.kategori, color = sipinjamBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(text = "Stok: ${barang.stok} unit", color = textSecondary, fontSize = 12.sp)
            }
            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (barang.tersedia) statusGreenBg else statusRedBg).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text(text = if (barang.tersedia) "Tersedia" else "Habis", color = if (barang.tersedia) statusGreen else statusRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(sipinjamBlue.copy(alpha = 0.1f)).clickable { onEditClick() }, contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = sipinjamBlue, modifier = Modifier.size(18.dp))
            }
            Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(statusRedBg).clickable { onDeleteClick() }, contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus", tint = statusRed, modifier = Modifier.size(18.dp))
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
    NavigationBar(containerColor = cardWhite, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = onDashboardClick,
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            label = { Text("DASHBOARD", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = sipinjamBlue, selectedTextColor = sipinjamBlue, unselectedIconColor = textSecondary, unselectedTextColor = textSecondary, indicatorColor = cardWhite)
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = onBarangClick,
            icon = { Icon(Icons.Filled.Inventory, contentDescription = null) },
            label = { Text("BARANG", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = sipinjamBlue, selectedTextColor = sipinjamBlue, unselectedIconColor = textSecondary, unselectedTextColor = textSecondary, indicatorColor = cardWhite)
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = onPermintaanClick,
            icon = { Icon(Icons.Filled.RequestPage, contentDescription = null) },
            label = { Text("PERMINTAAN", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = sipinjamBlue, selectedTextColor = sipinjamBlue, unselectedIconColor = textSecondary, unselectedTextColor = textSecondary, indicatorColor = cardWhite)
        )
        NavigationBarItem(
            selected = selected == 3,
            onClick = onProfilClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("PROFIL", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = sipinjamBlue, selectedTextColor = sipinjamBlue, unselectedIconColor = textSecondary, unselectedTextColor = textSecondary, indicatorColor = cardWhite)
        )
    }
}