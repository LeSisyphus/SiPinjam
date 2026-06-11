package com.example.sipinjam.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

import com.example.sipinjam.domain.repository.StorageRepository
class StorageRepositoryImpl(private val context: Context) : StorageRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun uploadFotoProfil(uri: Uri): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("User tidak ditemukan"))

        return suspendCancellableCoroutine { continuation ->
            MediaManager.get()
                .upload(uri)
                .option("folder", "foto_profil")
                .option("public_id", uid)
                .option("overwrite", true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            continuation.resume(Result.success(url))
                        } else {
                            continuation.resume(Result.failure(Exception("URL tidak ditemukan")))
                        }
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                })
                .dispatch(context)
        }
    }

    override suspend fun uploadFotoPengembalian(uri: Uri, peminjamanId: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get()
                .upload(uri)
                .option("folder", "foto_pengembalian")
                .option("public_id", peminjamanId)
                .option("overwrite", true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            continuation.resume(Result.success(url))
                        } else {
                            continuation.resume(Result.failure(Exception("URL tidak ditemukan")))
                        }
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                })
                .dispatch(context)
        }
    }
}
