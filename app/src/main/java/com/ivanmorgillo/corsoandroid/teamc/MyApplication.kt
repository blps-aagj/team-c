package com.ivanmorgillo.corsoandroid.teamc

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused") // gestire falso positivo
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}

/**
 * App module
 */
val appModule = module {
    single<RecipesRepository> {
        RecipeRepositoryImpl()
    }
    viewModel { MainViewModel(repository = get()) }
}
