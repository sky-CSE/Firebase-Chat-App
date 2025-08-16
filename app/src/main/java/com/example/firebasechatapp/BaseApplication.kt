package com.example.firebasechatapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.firebasechatapp.di.persistenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        // Always light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        startKoin {
            androidContext(this@BaseApplication)
            modules(persistenceModule)
        }
    }

    companion object {
        lateinit var context: BaseApplication
            private set
    }
}
