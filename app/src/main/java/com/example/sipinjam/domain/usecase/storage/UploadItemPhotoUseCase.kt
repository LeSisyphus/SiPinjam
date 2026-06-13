package com.example.sipinjam.domain.usecase.storage

import android.net.Uri
import com.example.sipinjam.domain.repository.StorageRepository

class UploadItemPhotoUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(uri: Uri) = repository.uploadFotoBarang(uri)
}
