package com.example.sipinjam.screens.user

import com.example.sipinjam.domain.model.ReturnStatus
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import com.example.sipinjam.domain.model.Pengembalian
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.pengembalian.AjukanPengembalianUseCase
import com.example.sipinjam.domain.usecase.pengembalian.GetPengembalianByPeminjamanIdUseCase
import com.example.sipinjam.domain.usecase.storage.UploadReturnPhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PengembalianViewModel(
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val getPengembalianByPeminjamanIdUseCase: GetPengembalianByPeminjamanIdUseCase,
    private val ajukanPengembalianUseCase: AjukanPengembalianUseCase,
    private val uploadReturnPhotoUseCase: UploadReturnPhotoUseCase,
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

    private val _catatanAdmin = MutableStateFlow<String?>(null)
    val catatanAdmin: StateFlow<String?> = _catatanAdmin.asStateFlow()

    fun muatBarang(barangId: String) {
        if (barangId.isBlank()) {
            _barang.value = null
            return
        }

        viewModelScope.launch {
            _isBarangLoading.value = true

            val data = getBarangDetailUseCase(barangId)
            _barang.value = data

            _isBarangLoading.value = false
        }
    }

    fun muatCatatanAdmin(peminjamanId: String) {
        viewModelScope.launch {
            val result = getPengembalianByPeminjamanIdUseCase(peminjamanId)

            result.onSuccess { pengembalian ->
                _catatanAdmin.value = if (
                    pengembalian?.status.equals(ReturnStatus.DITOLAK, ignoreCase = true) &&
                    !pengembalian?.catatanAdmin.isNullOrBlank()
                ) {
                    pengembalian?.catatanAdmin
                } else {
                    null
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

            val uploadResult = uploadReturnPhotoUseCase(fotoUri, peminjamanId)

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
                status = ReturnStatus.MENUNGGU_VERIFIKASI
            )

            val result = ajukanPengembalianUseCase(pengembalian)

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