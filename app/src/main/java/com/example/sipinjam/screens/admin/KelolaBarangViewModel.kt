package com.example.sipinjam.screens.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.sipinjam.domain.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class KelolaBarangUiState(
    val daftarBarang: List<BarangAdmin> = emptyList(),
    val searchQuery: String = "",
    val selectedKategori: String = "Semua",
    val showDeleteDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val barangToDelete: BarangAdmin? = null,
    val barangToEdit: BarangAdmin? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false
) {
    val filteredBarang: List<BarangAdmin>
        get() = daftarBarang.filter { barang ->
            val matchKategori = selectedKategori == "Semua" ||
                    barang.kategori.equals(selectedKategori, ignoreCase = true)
            val matchSearch = barang.nama.contains(searchQuery, ignoreCase = true) ||
                    barang.id.contains(searchQuery, ignoreCase = true)
            matchKategori && matchSearch
        }
}

class KelolaBarangViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(KelolaBarangUiState())
    val uiState: StateFlow<KelolaBarangUiState> = _uiState.asStateFlow()

    init {
        muatSemuaBarang()
    }

    fun muatSemuaBarang() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("items").get().await()
                val listBarang = snapshot.documents.map { document ->
                    BarangAdmin(
                        id = document.id,
                        nama = document.getString("nama") ?: "Tanpa Nama",
                        kategori = document.getString("kategori") ?: "UMUM",
                        stok = document.getLong("stok")?.toInt() ?: 0,
                        tersedia = document.getBoolean("tersedia") ?: true,
                        imageUrl = document.getString("fotoUrl") ?: ""
                    )
                }
                _uiState.update { it.copy(daftarBarang = listBarang, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    private suspend fun uploadKeCloudinary(context: Context, imageUri: Uri): String = suspendCancellableCoroutine { continuation ->
        try {
            try {
                MediaManager.get()
            } catch (e: IllegalStateException) {
            }

            MediaManager.get().upload(imageUri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String ?: ""
                        if (continuation.isActive) continuation.resume(secureUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception("Cloudinary Error: ${error.description}"))
                        }
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch(context)
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resumeWithException(e)
        }
    }

    fun onTambahBarangCloudinary(
        context: Context,
        nama: String, kategori: String, stok: Int, kondisi: String,
        lokasi: String, maksimalPinjam: String, deskripsi: String, imageUri: Uri?
    ) {
        _uiState.update { it.copy(isLoading = true, isSuccess = false, errorMessage = null) }
        viewModelScope.launch {
            try {
                var finalImageUrl = ""
                if (imageUri != null) {
                    finalImageUrl = uploadKeCloudinary(context, imageUri)
                }

                val docRef = firestore.collection("items").document()
                val barangBaru = Barang(
                    id = docRef.id,
                    nama = nama,
                    kategori = kategori,
                    stok = stok,
                    tersedia = stok > 0,
                    kondisi = kondisi,
                    lokasi = lokasi,
                    maksimalPinjam = maksimalPinjam,
                    deskripsi = deskripsi,
                    fotoUrl = finalImageUrl
                )
                docRef.set(barangBaru).await()

                muatSemuaBarang()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false, isEditSuccess = false, isDeleteSuccess = false) }
    }

    fun onEditRequest(barang: BarangAdmin) {
        _uiState.update { it.copy(showEditDialog = true, barangToEdit = barang) }
    }

    fun onEditDismiss() {
        _uiState.update { it.copy(showEditDialog = false, barangToEdit = null) }
    }

    fun onEditBarangFirestore(
        context: Context,
        id: String,
        nama: String,
        kategori: String,
        stok: Int,
        imageUri: Uri?
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val updateData = mutableMapOf<String, Any>(
                    "nama" to nama,
                    "kategori" to kategori,
                    "stok" to stok,
                    "tersedia" to (stok > 0)
                )

                if (imageUri != null) {
                    val newImageUrl = uploadKeCloudinary(context, imageUri)
                    updateData["fotoUrl"] = newImageUrl
                }

                firestore.collection("items").document(id)
                    .set(updateData, SetOptions.merge())
                    .await()

                muatSemuaBarang()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showEditDialog = false,
                        barangToEdit = null,
                        isEditSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onDeleteRequest(barang: BarangAdmin) {
        _uiState.update { it.copy(showDeleteDialog = true, barangToDelete = barang) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(showDeleteDialog = false, barangToDelete = null) }
    }

    fun onDeleteConfirm() {
        val barang = _uiState.value.barangToDelete ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                firestore.collection("items").document(barang.id).delete().await()
                muatSemuaBarang()
                _uiState.update { it.copy(isLoading = false, showDeleteDialog = false, barangToDelete = null, isDeleteSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun onSearchChange(query: String) { _uiState.update { it.copy(searchQuery = query) } }
    fun onKategoriChange(kategori: String) { _uiState.update { it.copy(selectedKategori = kategori) } }
}