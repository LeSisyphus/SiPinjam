package com.example.sipinjam.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.data.model.Pengembalian
import com.example.sipinjam.data.repository.PengembalianRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerifikasiPengembalianViewModel : ViewModel() {

    private val repository = PengembalianRepository()
    private val db = FirebaseFirestore.getInstance()

    private var pengembalianListenerJob: Job? = null

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
        if (pengembalianId.isBlank()) {
            _pengembalian.value = null
            _errorMessage.value = "ID pengembalian tidak valid"
            _isLoading.value = false
            return
        }

        pengembalianListenerJob?.cancel()

        pengembalianListenerJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.listenPengembalianById(pengembalianId).collect { data ->
                _pengembalian.value = data
                _isLoading.value = false

                if (data == null) {
                    _errorMessage.value = "Data pengembalian tidak ditemukan"
                    return@collect
                }

                if (data.status == "Ditolak") {
                    _catatanTolak.value = data.catatanAdmin
                } else {
                    _catatanTolak.value = null
                }
            }
        }
    }

    fun verifikasi(pengembalianId: String, catatan: String, kondisi: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _sukses.value = false

            val dataSaatIni = _pengembalian.value

            if (dataSaatIni == null) {
                _errorMessage.value = "Data pengembalian tidak ditemukan"
                _isLoading.value = false
                return@launch
            }

            if (pengembalianId.isBlank()) {
                _errorMessage.value = "ID pengembalian tidak valid"
                _isLoading.value = false
                return@launch
            }

            if (dataSaatIni.barangId.isBlank()) {
                _errorMessage.value = "ID barang pada pengembalian tidak valid"
                _isLoading.value = false
                return@launch
            }

            try {
                db.runTransaction { transaction ->
                    val pengembalianRef = db.collection("returns").document(pengembalianId)
                    val barangRef = db.collection("items").document(dataSaatIni.barangId)

                    val pengembalianSnap = transaction.get(pengembalianRef)
                    val barangSnap = transaction.get(barangRef)

                    if (!pengembalianSnap.exists()) {
                        throw IllegalStateException("Data pengembalian tidak ditemukan")
                    }

                    if (!barangSnap.exists()) {
                        throw IllegalStateException("Data barang tidak ditemukan")
                    }

                    val statusSaatIni = pengembalianSnap.getString("status") ?: ""
                    val peminjamanId = pengembalianSnap.getString("peminjamanId")
                        ?: dataSaatIni.peminjamanId

                    if (peminjamanId.isBlank()) {
                        throw IllegalStateException("ID peminjaman pada data pengembalian tidak ditemukan")
                    }

                    val peminjamanRef = db.collection("borrowings").document(peminjamanId)
                    val peminjamanSnap = transaction.get(peminjamanRef)

                    if (!peminjamanSnap.exists()) {
                        throw IllegalStateException("Data peminjaman tidak ditemukan")
                    }

                    when (statusSaatIni) {
                        "Terverifikasi" -> {
                            throw IllegalStateException("Pengembalian ini sudah diverifikasi sebelumnya")
                        }

                        "Ditolak" -> {
                            throw IllegalStateException("Pengembalian ini sudah ditolak sebelumnya")
                        }

                        "Menunggu Verifikasi" -> {
                            // Status valid, lanjut proses verifikasi.
                        }

                        else -> {
                            throw IllegalStateException(
                                "Pengembalian tidak bisa diverifikasi karena status saat ini: $statusSaatIni"
                            )
                        }
                    }

                    val stokSaatIni = barangSnap.getLong("stok")?.toInt() ?: 0
                    val stokBaru = stokSaatIni + 1

                    transaction.update(
                        pengembalianRef,
                        mapOf(
                            "status" to "Terverifikasi",
                            "catatanAdmin" to catatan,
                            "kondisiBarang" to kondisi
                        )
                    )

                    transaction.update(
                        barangRef,
                        mapOf(
                            "stok" to stokBaru,
                            "tersedia" to true
                        )
                    )

                    transaction.update(
                        peminjamanRef,
                        mapOf(
                            "status" to "Selesai"
                        )
                    )
                }.await()

                _sukses.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat verifikasi pengembalian"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun tolakPengembalian(pengembalianId: String, catatan: String) {
        viewModelScope.launch {
            if (catatan.isBlank()) {
                _errorMessage.value = "Catatan penolakan wajib diisi"
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            _sukses.value = false

            val dataSaatIni = _pengembalian.value

            if (dataSaatIni == null) {
                _errorMessage.value = "Data pengembalian tidak ditemukan"
                _isLoading.value = false
                return@launch
            }

            if (pengembalianId.isBlank()) {
                _errorMessage.value = "ID pengembalian tidak valid"
                _isLoading.value = false
                return@launch
            }

            try {
                db.runTransaction { transaction ->
                    val pengembalianRef = db.collection("returns").document(pengembalianId)
                    val pengembalianSnap = transaction.get(pengembalianRef)

                    if (!pengembalianSnap.exists()) {
                        throw IllegalStateException("Data pengembalian tidak ditemukan")
                    }

                    val statusSaatIni = pengembalianSnap.getString("status") ?: ""
                    val peminjamanId = pengembalianSnap.getString("peminjamanId")
                        ?: dataSaatIni.peminjamanId

                    if (peminjamanId.isBlank()) {
                        throw IllegalStateException("ID peminjaman pada data pengembalian tidak ditemukan")
                    }

                    val peminjamanRef = db.collection("borrowings").document(peminjamanId)
                    val peminjamanSnap = transaction.get(peminjamanRef)

                    if (!peminjamanSnap.exists()) {
                        throw IllegalStateException("Data peminjaman tidak ditemukan")
                    }

                    when (statusSaatIni) {
                        "Ditolak" -> {
                            throw IllegalStateException("Pengembalian ini sudah ditolak sebelumnya")
                        }

                        "Terverifikasi" -> {
                            throw IllegalStateException("Pengembalian yang sudah terverifikasi tidak bisa ditolak")
                        }

                        "Menunggu Verifikasi" -> {
                            // Status valid, lanjut proses penolakan.
                        }

                        else -> {
                            throw IllegalStateException(
                                "Pengembalian tidak bisa ditolak karena status saat ini: $statusSaatIni"
                            )
                        }
                    }

                    transaction.update(
                        pengembalianRef,
                        mapOf(
                            "status" to "Ditolak",
                            "catatanAdmin" to catatan,
                            "kondisiBarang" to ""
                        )
                    )

                    transaction.update(
                        peminjamanRef,
                        mapOf(
                            "status" to "Dipinjam"
                        )
                    )
                }.await()

                _catatanTolak.value = catatan
                _sukses.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat menolak pengembalian"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSukses() {
        _sukses.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        pengembalianListenerJob?.cancel()
    }
}