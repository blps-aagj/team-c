package com.blps.aagj.cookbook.domain.home

import Recipe
import RecipeByArea

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): LoadRecipesByAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// chiedere se deve essere spostata nel modulo di networking
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    private var cached: LoadRecipesByAreaResult? = null
    override suspend fun loadAllRecipesByArea(forced: Boolean): LoadRecipesByAreaResult {
        return if (cached == null || forced) {
            cached = loadFromNetwork()
            cached!!
        } else {
            cached!!
        }
    }

    private suspend fun loadFromNetwork(): LoadRecipesByAreaResult {
        return recipeAPI.loadAllRecipesByArea()
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}

sealed class LoadRecipesByAreaResult {
    data class Failure(val error: LoadRecipesByAreaError) : LoadRecipesByAreaResult()
    data class Success(val contentListRecipes: List<RecipeByArea>) : LoadRecipesByAreaResult()
}

sealed class LoadRecipesByAreaError {
    object NoInternetByArea : LoadRecipesByAreaError()
    object GenericErrorByArea : LoadRecipesByAreaError()
}

sealed class LoadRecipesError {
    object NoRecipeFound : LoadRecipesError()
    object NoInternet : LoadRecipesError()
    object GenericError : LoadRecipesError()
}

sealed class LoadRecipesResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipesResult()
    data class Failure(val error: LoadRecipesError) : LoadRecipesResult()
}
