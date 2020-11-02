package com.example.myapp

import android.app.Application


class MyApplication : Application() {
    companion object {
        lateinit var instansce: MyApplication
    }
    override fun onCreate() {
        super.onCreate()
        instansce = this
    }
}