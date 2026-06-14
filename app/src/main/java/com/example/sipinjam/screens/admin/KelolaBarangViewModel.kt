package com.example.sipinjam.screens.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipinjam.domain.model.Barang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import com.example.sipinjam.domain.usecase.barang.AddBarangUseCase
import com.example.sipinjam.domain.usecase.barang.DeleteBarangUseCase
import com.example.sipinjam.domain.usecase.barang.GetBarangDetailUseCase
import com.example.sipinjam.domain.usecase.barang.ObserveBarangListUseCase
import com.example.sipinjam.domain.usecase.barang.UpdateBarangUseCase
import com.example.sipinjam.domain.usecase.storage.UploadItemPhotoUseCase
import com.example.sipinjam.utils.UiMessageKey

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

class KelolaBarangViewModel(
    private val observeBarangListUseCase: ObserveBarangListUseCase,
    private val addBarangUseCase: AddBarangUseCase,
    private val updateBarangUseCase: UpdateBarangUseCase,
    private val deleteBarangUseCase: DeleteBarangUseCase,
    private val getBarangDetailUseCase: GetBarangDetailUseCase,
    private val uploadItemPhotoUseCase: UploadItemPhotoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(KelolaBarangUiState())
    val uiState: StateFlow<KelolaBarangUiState> = _uiState.asStateFlow()

    init {
        muatSemuaBarang()
    }

    fun muatSemuaBarang() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            observeBarangListUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = UiMessageKey.LOAD_ITEMS_FAILED
                        )
                    }
                }
                .collect { daftarBarang ->
                    val listBarang = daftarBarang.map { barang ->
                        BarangAdmin(
                            id = barang.id,
                            nama = barang.nama.ifBlank { "-" },
                            kategori = barang.kategori.ifBlank { "UMUM" },
                            stok = barang.stok,
                            tersedia = barang.tersedia,
                            imageUrl = barang.fotoUrl
                        )
                    }
                    _uiState.update { it.copy(daftarBarang = listBarang, isLoading = false) }
                }
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
                val finalImageUrl = imageUri?.let { uri ->
                    uploadItemPhotoUseCase(uri).getOrThrow()
                }.orEmpty()

                val barangBaru = Barang(
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

                val berhasil = addBarangUseCase(barangBaru)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = berhasil,
                        errorMessage = if (berhasil) null else UiMessageKey.ADD_ITEM_FAILED
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = UiMessageKey.GENERAL) }
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
                val existingBarang = getBarangDetailUseCase(id)
                val newImageUrl = imageUri?.let { uri ->
                    uploadItemPhotoUseCase(uri).getOrThrow()
                }

                val updatedBarang = (existingBarang ?: Barang(id = id)).copy(
                    id = id,
                    nama = nama,
                    kategori = kategori,
                    stok = stok,
                    tersedia = stok > 0,
                    fotoUrl = newImageUrl ?: existingBarang?.fotoUrl.orEmpty()
                )

                val berhasil = updateBarangUseCase(updatedBarang)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showEditDialog = !berhasil,
                        barangToEdit = if (berhasil) null else it.barangToEdit,
                        isEditSuccess = berhasil,
                        errorMessage = if (berhasil) null else UiMessageKey.EDIT_ITEM_FAILED
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = UiMessageKey.GENERAL) }
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
                val berhasil = deleteBarangUseCase(barang.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showDeleteDialog = !berhasil,
                        barangToDelete = if (berhasil) null else it.barangToDelete,
                        isDeleteSuccess = berhasil,
                        errorMessage = if (berhasil) null else UiMessageKey.DELETE_ITEM_FAILED
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = UiMessageKey.GENERAL) }
            }
        }
    }

    fun onSearchChange(query: String) { _uiState.update { it.copy(searchQuery = query) } }
    fun onKategoriChange(kategori: String) { _uiState.update { it.copy(selectedKategori = kategori) } }
}
