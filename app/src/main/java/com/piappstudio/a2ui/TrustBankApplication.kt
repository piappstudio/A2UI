package com.piappstudio.a2ui

import android.app.Application
import com.piappstudio.a2ui.di.AppContainer

class TrustBankApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
