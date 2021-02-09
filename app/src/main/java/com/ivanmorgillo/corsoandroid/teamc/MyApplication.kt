package com.ivanmorgillo.corsoandroid.teamc

import android.app.Application
import android.os.StrictMode
import com.ivanmorgillo.corsoandroid.teamb.CrashReportingTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused") // gestire falso positivo
class MyApplication : Application() {
    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree()) // stampa su logcat la linea di codice
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) { // affinché venga usato solo dalla modalità debug
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog() // log in logcat
                    .build()
            )
        }
    }
}
