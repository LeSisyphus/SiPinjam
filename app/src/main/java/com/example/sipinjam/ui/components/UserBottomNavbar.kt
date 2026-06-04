package com.example.sipinjam.ui.components

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.ui.theme.*

@Composable
fun UserBottomNavBar(
    selected: Int,
    onBerandaClick: () -> Unit,
    onKatalogClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onProfilClick: () -> Unit,
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = onBerandaClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_beranda), fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SiPinjamBlue,
                selectedTextColor = SiPinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = onKatalogClick,
            icon = { Icon(Icons.Filled.Book, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_katalog), fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SiPinjamBlue,
                selectedTextColor = SiPinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = onRiwayatClick,
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_riwayat), fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SiPinjamBlue,
                selectedTextColor = SiPinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = CardWhite
            )
        )
        NavigationBarItem(
            selected = selected == 3,
            onClick = onProfilClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_profil), fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SiPinjamBlue,
                selectedTextColor = SiPinjamBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = CardWhite
            )
        )
    }
}