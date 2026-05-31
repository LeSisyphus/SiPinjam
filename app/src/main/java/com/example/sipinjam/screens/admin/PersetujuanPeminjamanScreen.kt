package com.example.sipinjam.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.ui.components.AdminBottomNavBar
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary

private val RequestPageBg = Color(0xFFF7F7FF)
private val SoftBlueBg = Color(0xFFDCEBFF)
private val SoftItemBg = Color(0xFFF0F2FB)
private val DividerSoft = Color(0xFFE6E8F2)

private val RoleBlueBg = Color(0xFFD8E9FF)
private val RoleBlueText = Color(0xFF4F74A6)
private val RoleCyanBg = Color(0xFFD8F0FF)
private val RoleCyanText = Color(0xFF156D9E)
private val RoleOrangeBg = Color(0xFFFFE5C2)
private val RoleOrangeText = Color(0xFFA76012)

private val WaitingBg = Color(0xFFFFF1DA)
private val WaitingBorder = Color(0xFFEAB676)
private val WaitingText = Color(0xFF9A5B00)

@Composable
fun PersetujuanPeminjamanScreen(
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
    onDetailPengajuanClick: (peminjamanId: String) -> Unit = {},
    onVerifikasiClick: (pengembalianId: String) -> Unit = {},
    viewModel: PersetujuanPeminjamanViewModel = viewModel()
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val daftarPengembalian by viewModel.daftarPengembalian.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var tabAktif by remember { mutableIntStateOf(0) }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage.orEmpty())
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = RequestPageBg,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            AdminBottomNavBar(
                selected = 2,
                onDashboardClick = onDashboardClick,
                onBarangClick = onBarangClick,
                onPermintaanClick = onPermintaanClick,
                onProfilClick = onProfilClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RequestPageBg)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            HeaderSiPinjam()

            RequestTabs(
                selectedTab = tabAktif,
                onTabSelected = { tabAktif = it }
            )

            when (tabAktif) {
                0 -> {
                    if (isLoading && daftarPeminjaman.isEmpty()) {
                        LoadingContent()
                    } else if (daftarPeminjaman.isEmpty()) {
                        EmptyState(
                            text = "Belum ada permintaan peminjaman",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(26.dp)
                        ) {
                            items(
                                items = daftarPeminjaman,
                                key = { it.id }
                            ) { item ->
                                PersetujuanCard(
                                    item = item,
                                    onCardClick = {
                                        onDetailPengajuanClick(item.id)
                                    }
                                )
                            }

                            item {
                                Box(
                                    modifier = Modifier.windowInsetsBottomHeight(
                                        WindowInsets.navigationBars
                                    )
                                )
                            }
                        }
                    }
                }

                1 -> {
                    if (daftarPengembalian.isEmpty()) {
                        EmptyState(
                            text = "Belum ada pengembalian menunggu verifikasi",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(26.dp)
                        ) {
                            items(
                                items = daftarPengembalian,
                                key = { it.id }
                            ) { item ->
                                PengembalianRequestCard(
                                    item = item,
                                    onDetailClick = {
                                        onVerifikasiClick(item.id)
                                    }
                                )
                            }

                            item {
                                Box(
                                    modifier = Modifier.windowInsetsBottomHeight(
                                        WindowInsets.navigationBars
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSiPinjam() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.MenuBook,
            contentDescription = null,
            tint = SiPinjamBlue,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = "SiPinjam",
            color = SiPinjamBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RequestTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            RequestTabItem(
                title = "Persetujuan",
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            RequestTabItem(
                title = "Pengembalian",
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
        }

        Divider(
            modifier = Modifier.padding(top = 10.dp),
            color = DividerSoft,
            thickness = 1.dp
        )
    }
}

@Composable
private fun RequestTabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            color = if (selected) SiPinjamBlue else TextSecondary,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(width = 94.dp, height = 2.dp)
                .background(
                    color = if (selected) SiPinjamBlue else Color.Transparent,
                    shape = RoundedCornerShape(99.dp)
                )
        )
    }
}

@Composable
private fun PersetujuanCard(
    item: AdminPeminjamanUiItem,
    onCardClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            RequestUserHeader(
                namaUser = item.namaUser,
                role = item.roleUser,
                fotoUserUrl = item.fotoUserUrl,
                showMore = true
            )

            RequestItemBox(
                namaBarang = item.namaBarang,
                tanggal = item.tanggalLabel,
                fotoBarangUrl = item.fotoBarangUrl
            )

            Text(
                text = "Ketuk card untuk melihat detail pengajuan",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PengembalianRequestCard(
    item: AdminPengembalianUiItem,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp,
                    bottom = 18.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                RequestUserHeader(
                    namaUser = item.namaUser,
                    role = item.roleUser,
                    fotoUserUrl = item.fotoUserUrl,
                    showMore = false
                )

                ReturnItemBox(
                    namaBarang = item.namaBarang,
                    tanggal = item.tanggalLabel,
                    fotoBarangUrl = item.fotoBarangUrl,
                    status = item.status
                )
            }

            Button(
                onClick = onDetailClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SiPinjamBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Detail Pengembalian",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RequestUserHeader(
    namaUser: String,
    role: String,
    fotoUserUrl: String,
    showMore: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AvatarImage(
            imageUrl = fotoUserUrl,
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 3.dp)
        ) {
            Text(
                text = namaUser,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            RoleBadge(
                role = role,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        if (showMore) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Detail",
                tint = SiPinjamBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RequestItemBox(
    namaBarang: String,
    tanggal: String,
    fotoBarangUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SoftItemBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ItemImage(
            imageUrl = fotoBarangUrl,
            modifier = Modifier.size(54.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = namaBarang,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = tanggal,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReturnItemBox(
    namaBarang: String,
    tanggal: String,
    fotoBarangUrl: String,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoftItemBg)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ItemImage(
            imageUrl = fotoBarangUrl,
            modifier = Modifier.size(56.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = namaBarang,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = "Dikembalikan: $tanggal",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            StatusWaitingBadge(
                text = status.uppercase(),
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}

@Composable
private fun AvatarImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SoftBlueBg),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto peminjam",
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
private fun ItemImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto barang",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = null,
                tint = SiPinjamBlue,
                modifier = Modifier.size(26.dp)
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

@Composable
private fun StatusWaitingBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(7.dp),
        color = WaitingBg,
        border = BorderStroke(1.dp, WaitingBorder.copy(alpha = 0.65f))
    ) {
        Text(
            text = text,
            color = WaitingText,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = SiPinjamBlue)
    }
}

@Composable
private fun EmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PersetujuanPeminjamanScreenPreview() {
    MaterialTheme {
        PersetujuanPeminjamanScreen()
    }
}