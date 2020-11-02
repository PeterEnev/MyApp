package com.example.myapp

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


class MyApplication : Application() {
    companion object {
        lateinit var instansce: MyApplication
    }
    override fun onCreate() {
        super.onCreate()
        instansce = this
    }
}