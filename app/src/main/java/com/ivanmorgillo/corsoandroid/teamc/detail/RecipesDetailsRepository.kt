package com.ivanmorgillo.corsoandroid.teamc.detail

import com.ivanmorgillo.corsoandroid.teamc.detail.network.LoadRecipesDetailResult
import com.ivanmorgillo.corsoandroid.teamc.detail.network.RecipeDetailAPI

interface RecipesDetailsRepository {
    suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult
}

class RecipesDetailRepositoryImpl(private val recipeDetailAPI: RecipeDetailAPI) : RecipesDetailsRepository {
    override suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipe(id)
    }
}

data class RecipeDetail(
    val recipeName: String,
    val recipeCategory: String,
    val recipeArea: String,
    val recipeInstructions: List<String>,
    val recipeImage: String,
    val recipeIngredientsAndMeasures: List<Ingredient>,
    val recipeVideoInstructions: String
)

data class Ingredient(val ingredientName: String, val ingredientQuantity: String)
