package com.blps.aagj.cookbook.domain

import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): AllRecipesByAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// chiedere se deve essere spostata nel modulo di networking
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
        return recipeAPI.loadAllRecipesByArea()
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}

sealed class AllRecipesByAreaResult {
    data class Failure(val error: AllRecipesByAreaError) : AllRecipesByAreaResult()
    data class Success(val contentListRecipes: List<RecipeByArea>) : AllRecipesByAreaResult()

    sealed class AllRecipesByAreaError {
        object NoInternetByArea : AllRecipesByAreaError()
        object GenericErrorByArea : AllRecipesByAreaError()
    }
}
