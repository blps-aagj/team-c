package com.ivanmorgillo.corsoandroid.teamc.detail

import com.ivanmorgillo.corsoandroid.teamc.detail.network.LoadRecipesDetailResult
import com.ivanmorgillo.corsoandroid.teamc.detail.network.RecipeDetailAPI

interface RecipesDetailsRepository {
    suspend fun loadDetailsRecipes(id: Int): LoadRecipesDetailResult
}

class RecipesDetailRepositoryImpl(val recipeDetailAPI: RecipeDetailAPI) : RecipesDetailsRepository {
    override suspend fun loadDetailsRecipes(id: Int): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipe(id)
    }
}

data class RecipeDetail(
    val recipeName: String,
    val recipeCategory: String,
    val recipeArea: String,
    val recipeInstructions: String,
    val recipeImage: String,
    val recipeIngredientsAndMeasures: Map<RecipeIngredients, RecipeMeasures>,
    val recipeVideoInstructions: String
)

data class RecipeMeasures(val recipeMeasure: String)

data class RecipeIngredients(val recipeIngredient: String)
