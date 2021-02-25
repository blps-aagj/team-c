package com.ivanmorgillo.corsoandroid.teamc.detail

import com.ivanmorgillo.corsoandroid.teamc.network.detail.LoadRecipesDetailResult
import com.ivanmorgillo.corsoandroid.teamc.network.detail.RecipeDetailAPI

interface RecipesDetailsRepository {
    suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult
}

class RecipesDetailRepositoryImpl(private val recipeDetailAPI: RecipeDetailAPI) : RecipesDetailsRepository {
    override suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipe(id)
    }
}
