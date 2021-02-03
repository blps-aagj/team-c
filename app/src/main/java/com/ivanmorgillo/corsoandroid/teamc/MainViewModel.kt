package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.ViewModel

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

    fun getRecipes() = recipeList
}