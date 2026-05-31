package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.PeminjamanRepository
import com.example.sipinjam.data.repository.PengembalianRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerifikasiPengembalianViewModel : ViewModel() {

    private val repository = PengembalianRepository()
    private val peminjamanRepository = PeminjamanRepository()
    private val db = FirebaseFirestore.getInstance()

    private val _pengembalian = MutableStateFlow<Pengembalian?>(null)
    val pengembalian: StateFlow<Pengembalian?> = _pengembalian.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sukses = MutableStateFlow(false)
    val sukses: StateFlow<Boolean> = _sukses.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _catatanTolak = MutableStateFlow<String?>(null)
    val catatanTolak: StateFlow<String?> = _catatanTolak.asStateFlow()

    fun muatPengembalian(pengembalianId: String) {
        viewModelScope.launch {
            repository.listenPengembalianById(pengembalianId).collect { data ->
                _pengembalian.value = data
                if (data?.status == "Ditolak") {
                    _catatanTolak.value = data.catatanAdmin
                }
                if (data == null) {
                    _errorMessage.value = "Data pengembalian tidak ditemukan"
                }
            }
        }
    }

    fun verifikasi(pengembalianId: String, catatan: String, kondisi: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val data = _pengembalian.value
            if (data == null) {
                _errorMessage.value = "Data pengembalian tidak ditemukan"
                _isLoading.value = false
                return@launch
            }

            try {
                db.runTransaction { transaction ->
                    val pengembalianRef = db.collection("returns").document(pengembalianId)
                    val barangRef = db.collection("items").document(data.barangId)

                    val pengembalianSnap = transaction.get(pengembalianRef)
                    val barangSnap = transaction.get(barangRef)

                    val statusSaatIni = pengembalianSnap.getString("status") ?: ""

                    if (statusSaatIni == "Terverifikasi") {
                        throw Exception("Pengembalian ini sudah diverifikasi sebelumnya")
                    }

                    val stokSaatIni = barangSnap.getLong("stok")?.toInt() ?: 0
                    val stokBaru = stokSaatIni + 1

                    transaction.update(
                        pengembalianRef, mapOf(
                            "status" to "Terverifikasi",
                            "catatanAdmin" to catatan,
                            "kondisiBarang" to kondisi
                        )
                    )
                    transaction.update(
                        barangRef, mapOf(
                            "stok" to stokBaru,
                            "tersedia" to true
                        )
                    )
                }.await()

                data.peminjamanId.let { peminjamanId ->
                    peminjamanRepository.updateStatus(peminjamanId, "Selesai")
                }

                _sukses.value = true

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat verifikasi"
            }

            _isLoading.value = false
        }
    }

    fun tolak(pengembalianId: String, catatan: String) {
        viewModelScope.launch {
            if (catatan.isBlank()) {
                _errorMessage.value = "Catatan admin wajib diisi saat menolak pengembalian"
                return@launch
            }
            _isLoading.value = true
            _errorMessage.value = null

            val data = _pengembalian.value

            val result = repository.updateVerifikasi(
                id = pengembalianId,
                status = "Ditolak",
                catatanAdmin = catatan,
                kondisiBarang = ""
            )

            if (result.isSuccess) {
                data?.peminjamanId?.let {
                    peminjamanRepository.updateStatus(it, "Dipinjam")
                }
                _sukses.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }

            _isLoading.value = false
        }
    }

    fun resetState() {
        _sukses.value = false
        _errorMessage.value = null
    }
}