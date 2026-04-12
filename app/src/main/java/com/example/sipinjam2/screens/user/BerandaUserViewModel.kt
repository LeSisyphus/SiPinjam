package com.example.sipinjam2.ui.screens.user

import androidx.lifecycle.ViewModel
import com.example.sipinjam2.ui.screens.user.BarangTersedia
import com.example.sipinjam2.ui.screens.user.ItemDikembalikan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.filter

data class BerandaUiState(
    val barangTersedia: List<BarangTersedia> = listOf(
        BarangTersedia("Sony Alpha A7III", "DIGITAL IMAGING", ""),
        BarangTersedia("Sony Alpha A7III", "DIGITAL IMAGING", ""),
        BarangTersedia("Proyektor Epson", "ELEKTRONIK", ""),
    ),
    val itemDikembalikan: List<ItemDikembalikan> = listOf(
        ItemDikembalikan("HDMI Cable 5m", "Lab Multimedia", "12 Mei"),
        ItemDikembalikan("Tripod Excell", "Storage A", "08 Mei"),
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class BerandaUserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    fun onKembalikan(item: ItemDikembalikan) {
        _uiState.update { state ->
            state.copy(
                itemDikembalikan = state.itemDikembalikan.filter { it.nama != item.nama }
            )
        }
    }
}