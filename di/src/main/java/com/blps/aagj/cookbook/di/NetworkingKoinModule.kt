package com.blps.aagj.cookbook.di

import com.blps.aagj.cookbook.domain.RecipeAPI
import com.blps.aagj.cookbook.domain.RecipeDetailAPI
import com.blps.aagj.cookbook.networking.RecipeDetailAPIImpl
import com.blps.aagj.cookbook.networking.RecipeService
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPIImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkingKoinModule = module {
    single<RecipeService> {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        retrofit.create(RecipeService::class.java)
    }

    single<RecipeAPI> { // spiego a Koin come creare un RecipeAPI, ho soppresso il tipo per ridondanza
        RecipeAPIImpl(service = get())
    }

    single<RecipeDetailAPI> {
        RecipeDetailAPIImpl(service = get())
    }
}
