package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.ViewModel

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {

    private val recipeName = "Beef and Mustard pie"
    private val recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"

    private val recipeList = listOf(
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
        RecipeUI(
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl
        ),
    )

    /**
     * Get recipes
     *
     * @return List<RecipeUI>
     */
    fun getRecipes(): List<RecipeUI> = recipeList
}
