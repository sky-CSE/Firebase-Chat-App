package com.example.firebasechatapp.di


import com.example.firebasechatapp.data.local.SharedPrefs
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

/**
 * Use the [persistenceModule] to creating shared preference instance
 **/
val persistenceModule = module {
    single { SharedPrefs(androidContext()) }
}