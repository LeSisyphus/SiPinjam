package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.repository.BarangRepository
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KatalogUiState(
    val daftarBarang: List<Barang> = emptyList(),
    val filteredBarang: List<Barang> = emptyList(),
    val kategoriOptions: List<String> = listOf("Semua", "Elektronik", "Optik", "Kabel"),
    val selectedKategori: String = "Semua",
    val searchQuery: String = "",
    val selectedAvailability: String = "Semua",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class KatalogViewModel : ViewModel() {

    private val barangRepository = BarangRepositoryImpl()

    private val _uiState = MutableStateFlow(KatalogUiState())
    val uiState: StateFlow<KatalogUiState> = _uiState.asStateFlow()

    init {
        listenBarang()
    }

    private fun listenBarang() {
        viewModelScope.launch {
            barangRepository.getAllBarangRealTime()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Gagal memuat katalog barang"
                        )
                    }
                }
                .collect { daftarBarang ->
                    _uiState.update { currentState ->
                        val kategoriOptions = buildKategoriOptions(daftarBarang)
                        val filtered = filterBarang(
                            daftarBarang = daftarBarang,
                            selectedKategori = currentState.selectedKategori,
                            searchQuery = currentState.searchQuery,
                            selectedAvailability = currentState.selectedAvailability
                        )

                        currentState.copy(
                            daftarBarang = daftarBarang,
                            filteredBarang = filtered,
                            kategoriOptions = kategoriOptions,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun setInitialSearchQuery(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery == _uiState.value.searchQuery) return

        onSearchQueryChanged(normalizedQuery)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredBarang = filterBarang(
                    daftarBarang = currentState.daftarBarang,
                    selectedKategori = currentState.selectedKategori,
                    searchQuery = query,
                    selectedAvailability = currentState.selectedAvailability
                )
            )
        }
    }

    fun onKategoriSelected(kategori: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedKategori = kategori,
                filteredBarang = filterBarang(
                    daftarBarang = currentState.daftarBarang,
                    selectedKategori = kategori,
                    searchQuery = currentState.searchQuery,
                    selectedAvailability = currentState.selectedAvailability
                )
            )
        }
    }

    fun onAvailabilitySelected(availability: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAvailability = availability,
                filteredBarang = filterBarang(
                    daftarBarang = currentState.daftarBarang,
                    selectedKategori = currentState.selectedKategori,
                    searchQuery = currentState.searchQuery,
                    selectedAvailability = availability
                )
            )
        }
    }

    private fun filterBarang(
        daftarBarang: List<Barang>,
        selectedKategori: String,
        searchQuery: String,
        selectedAvailability: String
    ): List<Barang> {
        return daftarBarang
            .filter { barang ->
                val cocokKategori = selectedKategori.equals("Semua", ignoreCase = true) ||
                        barang.kategori.equals(selectedKategori, ignoreCase = true)

                val cocokSearch = searchQuery.isBlank() ||
                        barang.nama.contains(searchQuery, ignoreCase = true) ||
                        barang.kategori.contains(searchQuery, ignoreCase = true) ||
                        barang.lokasi.contains(searchQuery, ignoreCase = true)

                val isAvailable = barang.stok > 0 && barang.tersedia
                val cocokAvailability = when (selectedAvailability) {
                    "Tersedia" -> isAvailable
                    "Dipinjam" -> !isAvailable
                    else -> true
                }

                cocokKategori && cocokSearch && cocokAvailability
            }
            .sortedBy { it.nama.lowercase() }
    }

    private fun buildKategoriOptions(daftarBarang: List<Barang>): List<String> {
        val kategoriDesain = listOf("Elektronik", "Optik", "Kabel")

        val kategoriDariFirestore = daftarBarang
            .map { it.kategori.toDisplayKategori() }
            .filter { it.isNotBlank() }
            .distinct()

        val kategoriTambahan = kategoriDariFirestore
            .filterNot { kategoriFirestore ->
                kategoriDesain.any { kategoriDefault ->
                    kategoriDefault.equals(kategoriFirestore, ignoreCase = true)
                }
            }
            .sorted()

        return listOf("Semua") + kategoriDesain + kategoriTambahan
    }

    private fun String.toDisplayKategori(): String {
        return trim()
            .lowercase()
            .replaceFirstChar { char -> char.titlecase() }
    }
}