package com.mertsen.imdbproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ImdbProjectApplication:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}