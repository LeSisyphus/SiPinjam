package com.example.sipinjam2.ui.screens.admin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.filter

data class DashboardAdminUiState(
    val jumlahPermintaanMasuk: Int = 12,
    val jumlahTersedia: Int = 84,
    val jumlahDipinjam: Int = 5,
    val permintaanTerbaru: List<PermintaanItem> = listOf(
        PermintaanItem("Budi Darmawan", "Proyektor Epson X500", "Today, 09:15 AM"),
        PermintaanItem("Siti Aminah", "Kamera Sony Alpha A7", "Today, 08:30 AM"),
        PermintaanItem("Andi Wijaya", "Pointer Wireless R400", "Yesterday, 04:45 PM"),
        PermintaanItem("Andi Wijaya", "Pointer Wireless R400", "Yesterday, 04:45 PM"),
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class DashboardAdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardAdminUiState())
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()

    fun onTinjau(item: PermintaanItem) {
        _uiState.update { state ->
            state.copy(
                permintaanTerbaru = state.permintaanTerbaru.filter { it != item },
                jumlahPermintaanMasuk = state.jumlahPermintaanMasuk - 1
            )
        }
    }
}