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
        /**
         * nell'app dell'utente si crasha lui non avra niente
         */
        // quando la sviluppo
        if (BuildConfig.DEBUG) {
            // quando la mando su play store in modo tale da negare
            // l'accesso per evitare che ci rubino i dati
            Timber.plant(LineNumberDebugTree())
        } else {
            Timber.plant(CrashReportingTree())
            // firebase
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
