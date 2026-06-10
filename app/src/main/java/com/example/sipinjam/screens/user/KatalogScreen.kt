package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sipinjam.data.model.Barang
import com.example.sipinjam.ui.components.UserBottomNavBar
import com.example.sipinjam.ui.theme.BackgroundGray
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.DarkImageBg
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.TextPrimary
import com.example.sipinjam.ui.theme.TextSecondary
import com.example.sipinjam.ui.theme.ToggleBg
import com.example.sipinjam.ui.theme.StatusRedBg
import com.example.sipinjam.ui.theme.StatusBlue
import com.example.sipinjam.ui.theme.StatusBlueBg

@Composable
fun KatalogScreen(
    viewModel: KatalogViewModel = viewModel(),
    initialSearchQuery: String = "",
    onBarangClick: (Barang) -> Unit = {},
    onBerandaClick: () -> Unit = {},
    onKatalogClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onProfilClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialSearchQuery) {
        viewModel.setInitialSearchQuery(initialSearchQuery)
    }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            UserBottomNavBar(
                selected = 1,
                onBerandaClick = onBerandaClick,
                onKatalogClick = onKatalogClick,
                onRiwayatClick = onRiwayatClick,
                onProfilClick = onProfilClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(innerPadding)
        ) {
            KatalogHeader()

            Spacer(modifier = Modifier.height(24.dp))

            SearchAndFilterSection(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = viewModel::onSearchQueryChanged
            )

            Spacer(modifier = Modifier.height(22.dp))

            KategoriFilterSection(
                kategoriOptions = uiState.kategoriOptions,
                selectedKategori = uiState.selectedKategori,
                onKategoriSelected = viewModel::onKategoriSelected
            )

            Spacer(modifier = Modifier.height(22.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = SiPinjamBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    KatalogMessageCard(
                        message = uiState.errorMessage.orEmpty(),
                        isError = true,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                uiState.filteredBarang.isEmpty() -> {
                    KatalogMessageCard(
                        message = "Tidak ada barang yang sesuai.",
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        gridItems(
                            items = uiState.filteredBarang,
                            key = { barang -> barang.id }
                        ) { barang ->
                            KatalogBarangCard(
                                barang = barang,
                                onClick = { onBarangClick(barang) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KatalogHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(StatusBlueBg)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Book,
            contentDescription = null,
            tint = SiPinjamBlue,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = stringResource(R.string.app_name),
            color = SiPinjamBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_search_katalog),
                    color = TextSecondary.copy(alpha = 0.55f),
                    fontSize = 16.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                disabledContainerColor = CardWhite,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = SiPinjamBlue
            )
        )

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Filter",
                tint = SiPinjamBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun KategoriFilterSection(
    kategoriOptions: List<String>,
    selectedKategori: String,
    onKategoriSelected: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(kategoriOptions) { kategori ->
            KategoriChip(
                text = kategori,
                selected = selectedKategori.equals(kategori, ignoreCase = true),
                onClick = { onKategoriSelected(kategori) }
            )
        }
    }
}

@Composable
private fun KategoriChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(37.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) SiPinjamBlue else ToggleBg)
            .clickable { onClick() }
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun KatalogBarangCard(
    barang: Barang,
    onClick: () -> Unit,
) {
    val isAvailable = barang.stok > 0 && barang.tersedia
    val statusText = if (isAvailable) "Tersedia" else "Dipinjam"
    val statusColor = if (isAvailable) StatusGreen else StatusRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(DarkImageBg),
                contentAlignment = Alignment.Center
            ) {
                if (barang.fotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = barang.fotoUrl,
                        contentDescription = barang.nama,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = barang.kategori.uppercase(),
                    color = SiPinjamBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = barang.nama,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(statusColor)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${barang.stok.coerceAtLeast(0)} STOK",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun KatalogMessageCard(
    message: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isError) StatusRedBg else CardWhite)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = if (isError) StatusRed else TextSecondary,
            fontSize = 13.sp
        )
    }
}