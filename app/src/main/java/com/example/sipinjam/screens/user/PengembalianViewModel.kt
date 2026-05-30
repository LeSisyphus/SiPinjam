package com.example.sipinjam.screens.user

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.PengembalianRepository
import com.example.sipinjam.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PengembalianViewModel : ViewModel() {

    private val pengembalianRepository = PengembalianRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sukses = MutableStateFlow(false)
    val sukses: StateFlow<Boolean> = _sukses.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _catatanAdmin = MutableStateFlow<String?>(null)
    val catatanAdmin: StateFlow<String?> = _catatanAdmin.asStateFlow()

    fun muatCatatanAdmin(peminjamanId: String) {
        viewModelScope.launch {
            val result = pengembalianRepository.getPengembalianByPeminjamanId(peminjamanId)

            result.onSuccess { pengembalian ->
                if (
                    pengembalian?.status.equals("Ditolak", ignoreCase = true) &&
                    !pengembalian?.catatanAdmin.isNullOrBlank()
                ) {
                    _catatanAdmin.value = pengembalian?.catatanAdmin
                }
            }
        }
    }

    fun kirimPengembalian(
        context: Context,
        peminjamanId: String,
        barangId: String,
        userId: String,
        fotoUri: Uri,
        catatan: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val storageRepository = StorageRepository(context)
            val uploadResult = storageRepository.uploadFotoPengembalian(fotoUri, peminjamanId)

            if (uploadResult.isFailure) {
                _errorMessage.value = "Gagal upload foto: ${uploadResult.exceptionOrNull()?.message}"
                _isLoading.value = false
                return@launch
            }

            val fotoUrl = uploadResult.getOrDefault("")
            val tanggalKembali = SimpleDateFormat(
                "d MMMM yyyy",
                Locale("id", "ID")
            ).format(Date())

            val pengembalian = Pengembalian(
                peminjamanId = peminjamanId,
                userId = userId,
                barangId = barangId,
                fotoKondisiUrl = fotoUrl,
                catatan = catatan,
                catatanAdmin = "",
                kondisiBarang = "",
                tanggalKembali = tanggalKembali,
                status = "Menunggu Verifikasi"
            )

            val result = pengembalianRepository.ajukanPengembalianDanUpdatePeminjaman(
                pengembalian = pengembalian
            )

            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
                    ?: "Gagal mengajukan pengembalian"
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = false
            _sukses.value = true
        }
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }
}