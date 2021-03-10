package com.blps.aagj.cookbook.domain

interface RecipesDetailsRepository {
    suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult
    suspend fun loadDetailsRecipesRandom(): LoadRecipesDetailResult
}

class RecipesDetailRepositoryImpl(private val recipeDetailAPI: RecipeDetailAPI) : RecipesDetailsRepository {
    override suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipe(id)
    }

    override suspend fun loadDetailsRecipesRandom(): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipeRandom()
    }
}
