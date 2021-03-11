package com.blps.aagj.cookbook.domain.home

import Recipe
import RecipeByArea

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): LoadRecipesByAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
    suspend fun loadRecipesSearchByName(name: String): LoadRecipeSearchByNameResult
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

    override suspend fun loadRecipesSearchByName(name: String): LoadRecipeSearchByNameResult {
        return recipeAPI.loadRecipeSearchByName(name)
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

/* da spotare? */
sealed class LoadRecipeSearchByNameResult {
    data class Failure(val error: LoadRecipeSearchByNameError) : LoadRecipeSearchByNameResult()
    data class Success(val content: List<Recipe>) : LoadRecipeSearchByNameResult()
}

sealed class LoadRecipeSearchByNameError {
    object NoInternet : LoadRecipeSearchByNameError()
    object GenericError : LoadRecipeSearchByNameError()
}
