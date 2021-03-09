package com.ivanmorgillo.corsoandroid.teamc.detail

import com.blps.aagj.cookbook.domain.LoadRecipesDetailResult
import com.blps.aagj.cookbook.domain.RecipeDetailAPI

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
