package com.ivanmorgillo.corsoandroid.teamc

import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetailViewModel
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipesDetailRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipesDetailsRepository
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteRepository
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteViewModel
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.firebase.TrackingImpl
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamc.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.network.RecipeService
import com.ivanmorgillo.corsoandroid.teamc.network.detail.RecipeDetailAPIImpl
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPIImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * App module
 */
val appModule = module {
    /**
     * Singleton Pattern
     * Il single è un builder, rapp un'istanza che non viene creata e vive sempre uguale per tutta
     * l'esistenza dell'app (vogliamo che sia uno senza ambiguità); nelle parentesi angolari viene specificato
     * il tipo di ogg che voglio creare, poi viene specificato nelle graffe il "come" con la lambda
     */
    single<RecipesRepository> {
        RecipeRepositoryImpl(recipeAPI = RecipeAPIImpl(get()))
    }
    single { // spiego a Koin come creare un RecipeAPI, ho soppresso il tipo per ridondanza
        RecipeAPIImpl(service = get())
    }
    single<Tracking> {
        TrackingImpl()
    }
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
    single<RecipesDetailsRepository> {
        RecipesDetailRepositoryImpl(recipeDetailAPI = RecipeDetailAPIImpl(get()))
    }
    single<FavouriteRepository> {
        FavouriteRepositoryImpl()
    }
    viewModel {
        MainViewModel(
            repository = get(),
            tracking = get(),
            favouriteRepository = get(),
            detailsRepository = get()
        )
    } // Il get costruisce in base al tipo e a single
    viewModel { RecipeDetailViewModel(recipeDetailRepository = get(), tracking = get()) }
    viewModel { FavouriteViewModel(tracking = get(), repository = get()) }
}
