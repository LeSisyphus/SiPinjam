package com.example.sipinjam.screens.admin

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.sipinjam.ui.components.AdminBottomNavBar
import com.example.sipinjam.ui.components.SiPinjamTopBar
import com.example.sipinjam.ui.theme.*

data class PermintaanItem(
    val id: String = "",
    val nama: String,
    val namaBarang: String,
    val waktu: String,
    val fotoUserUrl: String = "",
)

@Composable
fun DashboardAdminScreen(
    viewModel: DashboardAdminViewModel = viewModel(),
    onLihatSemua: () -> Unit = {},
    onTinjau: (PermintaanItem) -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onBarangClick: () -> Unit = {},
    onPermintaanClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedNav by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AdminBottomNavBar(
                selected = selectedNav,
                onDashboardClick  = { selectedNav = 0; onDashboardClick() },
                onBarangClick     = { selectedNav = 1; onBarangClick() },
                onPermintaanClick = { selectedNav = 2; onPermintaanClick() },
                onProfilClick     = { selectedNav = 3; onProfilClick() },
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
                SiPinjamTopBar()
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = stringResource(R.string.screen_dashboard_requests), color = TextSecondary, fontSize = 14.sp)
                            Text(text = uiState.jumlahPermintaanMasuk.toString(), color = TextPrimary, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(InputBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.RequestPage, contentDescription = null, tint = SiPinjamBlue, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusCard(
                        label = stringResource(R.string.status_tersedia_upper),
                        jumlah = uiState.jumlahTersedia,
                        icon = Icons.Filled.CheckCircle,
                        iconColor = StatusGreen,
                        iconBgColor = StatusGreenBg,
                        modifier = Modifier.weight(1f)
                    )
                    StatusCard(
                        label = stringResource(R.string.status_dipinjam).uppercase(),
                        jumlah = uiState.jumlahDipinjam,
                        icon = Icons.Filled.Timer,
                        iconColor = StatusOrange,
                        iconBgColor = StatusOrangeBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.screen_dashboard_recent_requests), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(R.string.btn_lihat_semua),
                        color = SiPinjamBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onLihatSemua() }
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            items(uiState.permintaanTerbaru) { item ->
                PermintaanCard(
                    item = item,
                    onTinjau = {
                        viewModel.onTinjau(item)
                        onTinjau(item)
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    label: String,
    jumlah: Int,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Text(text = label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
            Text(text = jumlah.toString(), color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun PermintaanCard(item: PermintaanItem, onTinjau: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .background(InputBg),
            contentAlignment = Alignment.Center
        ) {
            if (item.fotoUserUrl.isNotBlank()) {
                AsyncImage(model = item.fotoUserUrl, contentDescription = stringResource(R.string.desc_profile_photo), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.nama, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = item.namaBarang, color = TextSecondary, fontSize = 12.sp)
            Text(text = item.waktu, color = TextSecondary, fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SiPinjamBlue)
                .clickable { onTinjau() }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(text = stringResource(R.string.btn_tinjau), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DashboardAdminScreenPreview() {
    MaterialTheme { DashboardAdminScreen() }
}