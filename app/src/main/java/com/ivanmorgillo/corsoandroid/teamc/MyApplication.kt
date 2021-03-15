package com.ivanmorgillo.corsoandroid.teamc

import android.app.Application
import android.os.StrictMode
import androidx.viewbinding.BuildConfig
import com.blps.aagj.cookbook.di.networkingKoinModule
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teamc.firebase.CrashReportingTree
import com.ivanmorgillo.corsoandroid.teamc.firebase.LineNumberDebugTree
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
            modules(appModule, networkingKoinModule)
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
        Firebase.database.setPersistenceEnabled(true)
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
