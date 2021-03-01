package com.ivanmorgillo.corsoandroid.teamc.home

import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.AllRecipesByAreaError
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.AllRecipesByAreaError.GenericError
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoInternet
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.ServerError
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPI

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): AllRecipesByAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// implementa l'interfaccia sopra
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    private var cached: AllRecipesByAreaResult? = null
    override suspend fun loadAllRecipesByArea(forced: Boolean): AllRecipesByAreaResult {
        return if (cached == null || forced) {
            cached = loadFromNetwork()
            cached!!
        } else {
            cached!!
        }
    }

    private suspend fun loadFromNetwork(): AllRecipesByAreaResult {
        val loadArea = recipeAPI.loadAreas()
        return when (loadArea) {
            is LoadAreaResult.Failure -> {
                when (loadArea.error) {
                    NoInternet -> Failure(AllRecipesByAreaError.NoInternet)
                    NoRecipeFound -> Failure(GenericError)
                    ServerError -> Failure(GenericError)
                    SlowInternet -> Failure(AllRecipesByAreaError.SlowInternet)
                }
            }
            is LoadAreaResult.Success -> {
                val areas = loadArea.areas
                val x = areas
                    .map { area ->
                        val result = loadRecipes(area.nameArea)
                        when (result) {
                            is LoadRecipesResult.Failure ->
                                when (result.error) {
                                    NoInternet -> Failure(AllRecipesByAreaError.NoInternet)
                                    NoRecipeFound -> Failure(GenericError)
                                    ServerError -> Failure(GenericError)
                                    SlowInternet -> Failure(AllRecipesByAreaError.SlowInternet)
                                }
                            is LoadRecipesResult.Success -> {
                                RecipeByArea(
                                    nameArea = area.nameArea,
                                    recipeByArea = result.recipes
                                )
                            }
                        }
                    }
                val isSuccessful = !x.any {
                    it is Failure
                }
                if (isSuccessful) {
                    val recipes = x.map {
                        it as RecipeByArea
                    }
                    AllRecipesByAreaResult.Success(recipes)
                } else {
                    val isNoInternet = x.any {
                        it is NoInternet
                    }
                    return if (isNoInternet) {
                        AllRecipesByAreaResult.Failure(AllRecipesByAreaError.NoInternet)
                    } else {
                        AllRecipesByAreaResult.Failure(GenericError)
                    }
                }
            }
        }.exhaustive
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}

sealed class AllRecipesByAreaResult {
    data class Failure(val error: AllRecipesByAreaError) : AllRecipesByAreaResult()
    data class Success(val contentListRecipes: List<RecipeByArea>) : AllRecipesByAreaResult()

    sealed class AllRecipesByAreaError {
        object NoInternet : AllRecipesByAreaError()
        object SlowInternet : AllRecipesByAreaError()
        object GenericError : AllRecipesByAreaError()
    }
}
