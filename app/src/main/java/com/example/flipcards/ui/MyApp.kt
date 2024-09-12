package com.example.flipcards.ui

import android.app.Application
import com.example.flipcards.di.AppModule
import com.example.flipcards.di.AppModuleImpl
import java.util.Locale

class MyApp: Application() {

    companion object{
        lateinit var appModule: AppModule
    }


    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        setLocaleToGerman()
    }

    private fun setLocaleToGerman() {
        val locale = Locale("de", "DE")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
    }

}