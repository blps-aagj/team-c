package com.ivanmorgillo.corsoandroid.teamc

import FavouriteRepository
import FavouriteRepositoryImpl
import RecipesDetailRepositoryImpl
import RecipesDetailsRepository
import com.blps.aagj.cookbook.domain.home.RecipeRepositoryImpl
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.google.gson.Gson
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetailViewModel
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteViewModel
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.firebase.TrackingImpl
import com.ivanmorgillo.corsoandroid.teamc.search.RecipeSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
        RecipeRepositoryImpl(recipeAPI = get())
    }

    single<Tracking> {
        TrackingImpl()
    }

    single<RecipesDetailsRepository> {
        RecipesDetailRepositoryImpl(recipeDetailAPI = get())
    }
    single<FavouriteRepository> {
        FavouriteRepositoryImpl(context = androidContext(), gson = Gson())
    }
    viewModel {
        MainViewModel(
            repository = get(),
            tracking = get(),
            favouriteRepository = get(),
            detailsRepository = get()
        )
    } // Il get costruisce in base al tipo e a single
    viewModel { RecipeDetailViewModel(recipeDetailRepository = get(), tracking = get(), favouriteRepository = get()) }
    viewModel { FavouriteViewModel(tracking = get(), repository = get()) }
    viewModel { RecipeSearchViewModel(repository = get(), favouriteRepository = get(), tracking = get()) }
}
