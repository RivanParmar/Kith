package com.kith

import android.app.Application
import com.kith.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KithApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Sync.initialize(this)
    }
}