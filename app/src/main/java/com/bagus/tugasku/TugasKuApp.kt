package com.bagus.tugasku

import android.app.Application
import com.bagus.tugasku.di.AppContainer

class TugasKuApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
