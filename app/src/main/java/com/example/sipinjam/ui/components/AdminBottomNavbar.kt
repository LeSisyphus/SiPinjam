package com.example.sipinjam.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.ui.theme.CardWhite
import com.example.sipinjam.ui.theme.SiPinjamBlue
import com.example.sipinjam.ui.theme.TextSecondary

@Composable
fun AdminBottomNavBar(
    selected: Int,
    onDashboardClick: () -> Unit,
    onBarangClick: () -> Unit,
    onPermintaanClick: () -> Unit,
    onProfilClick: () -> Unit,
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = onDashboardClick,
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            label = { Text("DASHBOARD", fontSize = 10.sp) },
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
            onClick = onBarangClick,
            icon = { Icon(Icons.Filled.Inventory, contentDescription = null) },
            label = { Text("BARANG", fontSize = 10.sp) },
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
            onClick = onPermintaanClick,
            icon = { Icon(Icons.Filled.RequestPage, contentDescription = null) },
            label = { Text("PERMINTAAN", fontSize = 10.sp) },
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
            label = { Text("PROFIL", fontSize = 10.sp) },
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