package com.example.sipinjam.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.repository.AuthRepository
import com.example.sipinjam.data.repository.BarangRepository
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.domain.model.Holiday
import com.example.sipinjam.domain.model.HolidayStatus
import com.example.sipinjam.domain.usecase.holiday.GetTodayHolidayUseCase
import com.example.sipinjam.domain.usecase.holiday.ObserveMonthlyHolidaysUseCase
import com.example.sipinjam.domain.usecase.holiday.RefreshMonthlyHolidaysUseCase
import java.util.Calendar
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BerandaUiState(
    val barangTersedia: List<BarangTersedia> = emptyList(),
    val barangTersediaAll: List<BarangTersedia> = emptyList(),
    val searchQuery: String = "",
    val itemDikembalikan: List<ItemDikembalikan> = emptyList(),
    val todayHolidayStatus: HolidayStatus? = null,
    val monthlyHolidays: List<Holiday> = emptyList(),
    val isLoading: Boolean = false,
    val isHolidayLoading: Boolean = false,
    val errorMessage: String? = null,
    val holidayErrorMessage: String? = null,
)

class BerandaUserViewModel(
    private val getTodayHolidayUseCase: GetTodayHolidayUseCase? = null,
    private val observeMonthlyHolidaysUseCase: ObserveMonthlyHolidaysUseCase? = null,
    private val refreshMonthlyHolidaysUseCase: RefreshMonthlyHolidaysUseCase? = null,
    private val barangRepository: BarangRepository = BarangRepository(),
    private val peminjamanRepository: PeminjamanRepository = PeminjamanRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    init {
        fetchBarangRealTime()
        fetchItemPerluDikembalikanRealTime()
        observeHolidayCache()
        refreshHolidayInfo()
    }

    fun onSearchChange(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.barangTersediaAll
        } else {
            _uiState.value.barangTersediaAll.filter {
                it.nama.contains(query, ignoreCase = true) ||
                        it.kategori.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(searchQuery = query, barangTersedia = filtered) }
    }

    fun refreshHolidayInfo() {
        val refreshUseCase = refreshMonthlyHolidaysUseCase ?: return
        val todayUseCase = getTodayHolidayUseCase ?: return

        viewModelScope.launch {
            val (year, month) = currentYearMonth()
            _uiState.update {
                it.copy(
                    isHolidayLoading = true,
                    holidayErrorMessage = null,
                )
            }

            val refreshResult = refreshUseCase(year, month)
            val todayResult = todayUseCase()

            _uiState.update { currentState ->
                currentState.copy(
                    todayHolidayStatus = todayResult.getOrNull() ?: currentState.todayHolidayStatus,
                    isHolidayLoading = false,
                    holidayErrorMessage = when {
                        refreshResult.isFailure && todayResult.isFailure -> {
                            refreshResult.exceptionOrNull()?.localizedMessage
                                ?: todayResult.exceptionOrNull()?.localizedMessage
                                ?: "Gagal memuat info hari libur"
                        }
                        else -> null
                    }
                )
            }
        }
    }

    private fun observeHolidayCache() {
        val observeUseCase = observeMonthlyHolidaysUseCase ?: return

        viewModelScope.launch {
            val (year, month) = currentYearMonth()
            observeUseCase(year, month)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            holidayErrorMessage = exception.localizedMessage
                                ?: "Gagal memuat cache hari libur"
                        )
                    }
                }
                .collect { cachedHolidays ->
                    _uiState.update {
                        it.copy(monthlyHolidays = cachedHolidays)
                    }
                }
        }
    }

    private fun fetchBarangRealTime() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            barangRepository.getAllBarangRealTime()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Gagal memuat barang"
                        )
                    }
                }
                .collect { listBarangFirestore ->
                    val barangTersediaMapped = listBarangFirestore
                        .filter { it.stok > 0 }
                        .map { barangDoc ->
                            BarangTersedia(
                                id = barangDoc.id,
                                nama = barangDoc.nama,
                                kategori = barangDoc.kategori.uppercase(),
                                imageUrl = barangDoc.fotoUrl
                            )
                        }

                    val currentQuery = _uiState.value.searchQuery
                    val filtered = if (currentQuery.isBlank()) {
                        barangTersediaMapped
                    } else {
                        barangTersediaMapped.filter {
                            it.nama.contains(currentQuery, ignoreCase = true) ||
                                    it.kategori.contains(currentQuery, ignoreCase = true)
                        }
                    }

                    _uiState.update {
                        it.copy(
                            barangTersediaAll = barangTersediaMapped,
                            barangTersedia = filtered,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun fetchItemPerluDikembalikanRealTime() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()

            if (currentUser == null) {
                _uiState.update {
                    it.copy(errorMessage = "User tidak ditemukan, silakan login ulang")
                }
                return@launch
            }

            peminjamanRepository.listenPeminjamanByUser(currentUser.uid)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            errorMessage = exception.localizedMessage ?: "Gagal memuat data peminjaman"
                        )
                    }
                }
                .collect { daftarPeminjaman ->
                    val daftarPerluDikembalikan = daftarPeminjaman
                        .filter { peminjaman ->
                            peminjaman.status.equals("Disetujui", ignoreCase = true) ||
                                    peminjaman.status.equals("Dipinjam", ignoreCase = true)
                        }
                        .map { peminjaman ->
                            async {
                                val barang = barangRepository.getBarangById(peminjaman.barangId)

                                ItemDikembalikan(
                                    peminjamanId = peminjaman.id,
                                    barangId = peminjaman.barangId,
                                    userId = peminjaman.userId,
                                    nama = barang?.nama?.takeIf { it.isNotBlank() }
                                        ?: peminjaman.namaBarang.ifBlank { "Barang" },
                                    lokasi = barang?.lokasi?.takeIf { it.isNotBlank() } ?: "-",
                                    tanggalPinjam = peminjaman.tanggalPinjam,
                                    tanggalJatuhTempo = peminjaman.tanggalKembali,
                                    imageUrl = barang?.fotoUrl.orEmpty()
                                )
                            }
                        }
                        .awaitAll()

                    _uiState.update {
                        it.copy(itemDikembalikan = daftarPerluDikembalikan)
                    }
                }
        }
    }

    private fun currentYearMonth(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) to (calendar.get(Calendar.MONTH) + 1)
    }
}