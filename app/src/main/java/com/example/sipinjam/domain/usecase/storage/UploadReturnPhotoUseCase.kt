package com.example.sipinjam.domain.usecase.storage

import android.net.Uri
import com.example.sipinjam.domain.repository.StorageRepository

class UploadReturnPhotoUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(uri: Uri, peminjamanId: String) =
        repository.uploadFotoPengembalian(uri, peminjamanId)
}
