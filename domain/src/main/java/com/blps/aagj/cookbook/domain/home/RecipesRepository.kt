package com.blps.aagj.cookbook.domain.home

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
