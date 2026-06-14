package com.example.sipinjam.screens.admin

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sipinjam.ui.components.AdminBottomNavBar
import com.example.sipinjam.ui.components.CloudinaryImage
import com.example.sipinjam.ui.components.SiPinjamTopBar
import com.example.sipinjam.ui.components.localizedRoleText
import com.example.sipinjam.ui.components.localizedStatusText
import com.example.sipinjam.ui.components.localizedUiMessage
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DividerColor
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusBlue
import com.example.sipinjam.ui.theme.StatusBlueBg
import com.example.sipinjam.ui.theme.StatusOrange
import com.example.sipinjam.ui.theme.StatusOrangeBg
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary
import com.example.sipinjam.di.rememberSiPinjamViewModelFactory

private val RequestPageBg: Color
    @Composable get() = BackgroundGray

private val SoftItemBg: Color
    @Composable get() = InputBg

private val DividerSoft: Color
    @Composable get() = DividerColor

private val RoleBlueBg: Color
    @Composable get() = StatusBlueBg

private val RoleBlueText: Color
    @Composable get() = StatusBlue

private val RoleCyanBg: Color
    @Composable get() = StatusBlueBg

private val RoleCyanText: Color
    @Composable get() = StatusBlue

private val RoleOrangeBg: Color
    @Composable get() = StatusOrangeBg

private val RoleOrangeText: Color
    @Composable get() = StatusOrange

private val WaitingBg: Color
    @Composable get() = StatusOrangeBg

private val WaitingBorder: Color
    @Composable get() = StatusOrange

private val WaitingText: Color
    @Composable get() = StatusOrange

@Composable
fun PersetujuanPeminjamanScreen(
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
    onDetailPengajuanClick: (peminjamanId: String) -> Unit = {},
    onVerifikasiClick: (pengembalianId: String) -> Unit = {},
    viewModel: PersetujuanPeminjamanViewModel = viewModel(factory = rememberSiPinjamViewModelFactory())
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val daftarPengembalian by viewModel.daftarPengembalian.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var tabAktif by remember { mutableIntStateOf(0) }

    val localizedErrorMessage = localizedUiMessage(errorMessage)

    LaunchedEffect(localizedErrorMessage) {
        if (localizedErrorMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(localizedErrorMessage)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = RequestPageBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            SiPinjamTopBar()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
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
                                text = stringResource(R.string.empty_permohonan),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(
                                    top = 20.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(26.dp)
                            ) {
                                items(
                                    items = daftarPeminjaman,
                                    key = { it.id }
                                ) { item ->
                                    PersetujuanCardModern(
                                        item = item,
                                        onDetailClick = {
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
                                text = stringResource(R.string.empty_returns_waiting_verification),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(
                                    top = 20.dp,
                                    bottom = 24.dp
                                ),
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
}

@Composable
private fun RequestTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            RequestTabItem(
                title = stringResource(R.string.tab_persetujuan),
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            RequestTabItem(
                title = stringResource(R.string.tab_pengembalian),
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
private fun PersetujuanCardModern(
    item: AdminPeminjamanUiItem,
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
                    fotoUserUrl = item.fotoUserUrl
                )

                ApprovalItemBox(
                    namaBarang = item.namaBarang,
                    tanggal = item.tanggalLabel,
                    fotoBarangUrl = item.fotoBarangUrl
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
                colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.screen_approval_detail),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
                    fotoUserUrl = item.fotoUserUrl
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
                colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.screen_return_detail),
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
    fotoUserUrl: String
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
    }
}

@Composable
private fun ApprovalItemBox(
    namaBarang: String,
    tanggal: String,
    fotoBarangUrl: String
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
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = tanggal,
                    color = TextSecondary,
                    fontSize = 12.sp,
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
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = stringResource(R.string.label_returned_date, tanggal),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            StatusWaitingBadge(
                text = localizedStatusText(status).uppercase(),
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
    CloudinaryImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(R.string.desc_borrower_photo),
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        placeholderSize = 24.dp
    )
}

@Composable
private fun ItemImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    CloudinaryImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(R.string.desc_item_photo),
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        placeholderSize = 26.dp
    )
}

@Composable
private fun RoleBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val normalizedRole = role.uppercase()

    val badgeColor = when (normalizedRole) {
        "DOSEN" -> RoleCyanBg
        "STAF", "STAFF" -> RoleOrangeBg
        else -> RoleBlueBg
    }

    val textColor = when (normalizedRole) {
        "DOSEN" -> RoleCyanText
        "STAF", "STAFF" -> RoleOrangeText
        else -> RoleBlueText
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(badgeColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = localizedRoleText(role),
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
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = RoundedCornerShape(7.dp),
        color = WaitingBg,
        border = BorderStroke(
            width = 1.dp,
            color = WaitingBorder.copy(alpha = 0.65f)
        )
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
