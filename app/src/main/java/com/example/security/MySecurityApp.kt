package com.example.security

import android.app.Application
import com.example.security.di.appModule
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MySecurityApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MySecurityApp)
            modules(appModule)
        }
    }
}