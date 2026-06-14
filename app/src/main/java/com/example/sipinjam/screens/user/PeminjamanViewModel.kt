package com.example.sipinjam.screens.user

import com.example.sipinjam.domain.model.BorrowingStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.usecase.auth.GetCurrentUserUseCase
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.peminjaman.AjukanPeminjamanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.example.sipinjam.utils.UiMessageKey

class PeminjamanViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val ajukanPeminjamanUseCase: AjukanPeminjamanUseCase,
) : ViewModel() {

    private val _barang = MutableStateFlow<Barang?>(null)
    val barang: StateFlow<Barang?> = _barang.asStateFlow()

    private val _isBarangLoading = MutableStateFlow(false)
    val isBarangLoading: StateFlow<Boolean> = _isBarangLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sukses = MutableStateFlow(false)
    val sukses: StateFlow<Boolean> = _sukses.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadBarang(barangId: String) {
        if (barangId.isBlank()) {
            _errorMessage.value = UiMessageKey.INVALID_ITEM_ID
            return
        }

        viewModelScope.launch {
            _isBarangLoading.value = true
            _errorMessage.value = null

            val result = getBarangDetailUseCase(barangId)

            if (result == null) {
                _errorMessage.value = UiMessageKey.ITEM_NOT_FOUND
            } else {
                _barang.value = result
            }

            _isBarangLoading.value = false
        }
    }

    fun kirimPermohonan(
        barangId: String,
        namaBarang: String,
        tanggalPinjam: String,
        tanggalKembali: String,
        keperluan: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            if (barangId.isBlank()) {
                gagal(UiMessageKey.INVALID_ITEM_ID)
                return@launch
            }

            if (tanggalPinjam.isBlank()) {
                gagal(UiMessageKey.BORROW_DATE_REQUIRED)
                return@launch
            }

            if (tanggalKembali.isBlank()) {
                gagal(UiMessageKey.RETURN_DATE_REQUIRED)
                return@launch
            }

            if (keperluan.isBlank()) {
                gagal(UiMessageKey.BORROW_REASON_REQUIRED)
                return@launch
            }

            val currentUser = getCurrentUserUseCase()
            if (currentUser == null) {
                gagal(UiMessageKey.USER_NOT_FOUND_LOGIN_AGAIN)
                return@launch
            }

            val barangTerbaru = getBarangDetailUseCase(barangId)
            if (barangTerbaru == null) {
                gagal(UiMessageKey.ITEM_NOT_FOUND)
                return@launch
            }

            _barang.value = barangTerbaru

            if (barangTerbaru.stok <= 0 || !barangTerbaru.tersedia) {
                gagal(UiMessageKey.ITEM_UNAVAILABLE_OR_OUT_OF_STOCK)
                return@launch
            }

            val tanggalPinjamDate = parseTanggal(tanggalPinjam)
            val tanggalKembaliDate = parseTanggal(tanggalKembali)

            if (tanggalPinjamDate == null || tanggalKembaliDate == null) {
                gagal(UiMessageKey.DATE_FORMAT_INVALID)
                return@launch
            }

            val hariIni = tanggalHariIni()

            if (tanggalPinjamDate.before(hariIni)) {
                gagal(UiMessageKey.BORROW_DATE_PAST)
                return@launch
            }

            if (tanggalKembaliDate.before(tanggalPinjamDate)) {
                gagal(UiMessageKey.RETURN_DATE_BEFORE_BORROW_DATE)
                return@launch
            }

            val durasiHari = hitungDurasiHari(
                tanggalPinjam = tanggalPinjamDate,
                tanggalKembali = tanggalKembaliDate
            )

            val maksimalPinjam = parseMaksimalPinjam(barangTerbaru.maksimalPinjam)

            if (maksimalPinjam > 0 && durasiHari > maksimalPinjam) {
                gagal(UiMessageKey.maxBorrowDuration(maksimalPinjam))
                return@launch
            }

            val peminjaman = Peminjaman(
                userId = currentUser.uid,
                namaUser = currentUser.nama,
                barangId = barangId,
                namaBarang = barangTerbaru.nama.ifBlank { namaBarang },
                tanggalPinjam = tanggalPinjam,
                tanggalKembali = tanggalKembali,
                keperluan = keperluan.trim(),
                status = BorrowingStatus.DIPROSES,
                createdAt = System.currentTimeMillis()
            )

            val result = ajukanPeminjamanUseCase(peminjaman)

            _isLoading.value = false

            if (result.isSuccess) {
                _sukses.value = true
            } else {
                _errorMessage.value = UiMessageKey.SEND_BORROW_REQUEST_FAILED
            }
        }
    }

    private fun gagal(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
    }

    private fun parseTanggal(tanggal: String): Date? {
        return try {
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
            formatter.isLenient = false
            formatter.parse(tanggal)
        } catch (e: Exception) {
            null
        }
    }

    private fun tanggalHariIni(): Date {
        val calendar = Calendar.getInstance(Locale("id", "ID"))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun hitungDurasiHari(
        tanggalPinjam: Date,
        tanggalKembali: Date
    ): Int {
        val diffMillis = tanggalKembali.time - tanggalPinjam.time
        val selisihHari = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()

        return selisihHari + 1
    }

    private fun parseMaksimalPinjam(value: String): Int {
        return value
            .filter { it.isDigit() }
            .toIntOrNull()
            ?: 0
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }
}
