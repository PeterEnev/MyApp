package com.example.myapp

import android.app.Application
import android.content.res.Configuration


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
    }

    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}