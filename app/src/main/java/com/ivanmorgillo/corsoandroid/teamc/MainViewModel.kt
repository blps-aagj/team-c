package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.ViewModel

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {
    private val recipeList = listOf<RecipeUI>(
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
        RecipeUI(
            recipeName = "Beef and Mustard pie",
            recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        ),
    )

    /**
     * Get recipes
     *
     * @return List<RecipeUI>
     */
    fun getRecipes(): List<RecipeUI> = recipeList
}