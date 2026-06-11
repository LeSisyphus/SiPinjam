package com.example.sipinjam.screens.user

import com.example.sipinjam.domain.model.BorrowingStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Peminjaman
import com.example.sipinjam.domain.repository.AuthRepository
import com.example.sipinjam.data.repository.AuthRepositoryImpl
import com.example.sipinjam.domain.repository.BarangRepository
import com.example.sipinjam.data.repository.BarangRepositoryImpl
import com.example.sipinjam.domain.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PeminjamanRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PeminjamanViewModel : ViewModel() {

    private val repository = PeminjamanRepositoryImpl()
    private val barangRepository = BarangRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()

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
            _errorMessage.value = "ID barang tidak valid"
            return
        }

        viewModelScope.launch {
            _isBarangLoading.value = true
            _errorMessage.value = null

            val result = barangRepository.getBarangById(barangId)

            if (result == null) {
                _errorMessage.value = "Data barang tidak ditemukan"
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
                gagal("ID barang tidak valid")
                return@launch
            }

            if (tanggalPinjam.isBlank()) {
                gagal("Tanggal pinjam wajib dipilih")
                return@launch
            }

            if (tanggalKembali.isBlank()) {
                gagal("Tanggal kembali wajib dipilih")
                return@launch
            }

            if (keperluan.isBlank()) {
                gagal("Keperluan peminjaman wajib diisi")
                return@launch
            }

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                gagal("User tidak ditemukan, silakan login ulang")
                return@launch
            }

            val barangTerbaru = barangRepository.getBarangById(barangId)
            if (barangTerbaru == null) {
                gagal("Data barang tidak ditemukan")
                return@launch
            }

            _barang.value = barangTerbaru

            if (barangTerbaru.stok <= 0 || !barangTerbaru.tersedia) {
                gagal("Barang tidak tersedia atau stok sudah habis")
                return@launch
            }

            val tanggalPinjamDate = parseTanggal(tanggalPinjam)
            val tanggalKembaliDate = parseTanggal(tanggalKembali)

            if (tanggalPinjamDate == null || tanggalKembaliDate == null) {
                gagal("Format tanggal tidak valid")
                return@launch
            }

            val hariIni = tanggalHariIni()

            if (tanggalPinjamDate.before(hariIni)) {
                gagal("Tanggal pinjam tidak boleh sebelum hari ini")
                return@launch
            }

            if (tanggalKembaliDate.before(tanggalPinjamDate)) {
                gagal("Tanggal kembali tidak boleh sebelum tanggal pinjam")
                return@launch
            }

            val durasiHari = hitungDurasiHari(
                tanggalPinjam = tanggalPinjamDate,
                tanggalKembali = tanggalKembaliDate
            )

            val maksimalPinjam = parseMaksimalPinjam(barangTerbaru.maksimalPinjam)

            if (maksimalPinjam > 0 && durasiHari > maksimalPinjam) {
                gagal("Durasi peminjaman maksimal $maksimalPinjam hari")
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

            val result = repository.tambahPeminjaman(peminjaman)

            _isLoading.value = false

            if (result.isSuccess) {
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
                    ?: "Gagal mengirim permohonan"
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