package com.example.sipinjam

import android.app.Application
import com.cloudinary.android.MediaManager
import com.example.sipinjam.di.AppContainer

class SiPinjamApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
        initCloudinary()
    }

    private fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
        )

        runCatching {
            MediaManager.init(this, config)
        }
    }
}
