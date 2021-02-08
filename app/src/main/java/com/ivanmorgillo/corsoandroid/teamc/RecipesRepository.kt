package com.ivanmorgillo.corsoandroid.teamc

private const val MAXRANGE = 10 // costante per potere togliere il problema del magic number

interface RecipesRepository {
    suspend fun loadRecipes(): List<Recipe>
}

class RecipeRepositoryImpl : RecipesRepository {
    override suspend fun loadRecipes(): List<Recipe> {
        val recipeName = "Beef and Mustard pie"
        val recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
        return (1..MAXRANGE).map {
            Recipe(
                name = recipeName + it,
                image = recipeImageUrl,
                idMeal = it.toString(),
            )
        }
    }
}

data class Recipe(val name: String, val image: String, val idMeal: String)
