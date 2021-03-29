package com.blps.aagj.cookbook.domain.home

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): LoadRecipesByAreaResult
    suspend fun loadRecipesArea(area: String): LoadRecipesResult
    suspend fun loadRecipesSearchByName(name: String): LoadRecipeSearchByNameResult
    suspend fun loadRecipesCategory(category: String): LoadRecipesResult
    suspend fun loadAllRecipesByCategory(forced: Boolean = false): LoadRecipesByCategoryResult
    suspend fun loadRecipeByIngredient(ingredient: String): LoadRecipeSearchByNameResult
}

// chiedere se deve essere spostata nel modulo di networking
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    private var areaCached: LoadRecipesByAreaResult? = null
    private var categoryCached: LoadRecipesByCategoryResult? = null
    override suspend fun loadAllRecipesByArea(forced: Boolean): LoadRecipesByAreaResult {
        return if (areaCached == null || forced) {
            areaCached = loadFromNetwork()
            areaCached!!
        } else {
            areaCached!!
        }
    }

    private suspend fun loadFromNetwork(): LoadRecipesByAreaResult {
        return recipeAPI.loadAllRecipesByArea()
    }

    override suspend fun loadRecipesArea(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipesByArea(area)
    }

    override suspend fun loadRecipesSearchByName(name: String): LoadRecipeSearchByNameResult {
        return recipeAPI.loadRecipeSearchByName(name)
    }

    override suspend fun loadRecipesCategory(category: String): LoadRecipesResult {
        return recipeAPI.loadRecipeByCategories(category)
    }

    override suspend fun loadAllRecipesByCategory(forced: Boolean): LoadRecipesByCategoryResult {
        return if (categoryCached == null || forced) {
            categoryCached = recipeAPI.loadAllRecipesByCategory()
            categoryCached!!
        } else {
            categoryCached!!
        }
    }

    override suspend fun loadRecipeByIngredient(ingredient: String): LoadRecipeSearchByNameResult {
        return recipeAPI.loadRecipesByIngredient(ingredient)
    }
}
