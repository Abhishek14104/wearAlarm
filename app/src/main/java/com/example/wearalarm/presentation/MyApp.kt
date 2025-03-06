package com.example.wearalarm

import android.app.Application

class MyApp : Application() {
    companion object {
        private var _instance: MyApp? = null

        val instance: MyApp
            get() = _instance ?: throw IllegalStateException("Application instance not initialized yet.")
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}