package com.ivanmorgillo.corsoandroid.teamc

import com.ivanmorgillo.corsoandroid.teamc.network.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.RecipeAPI

interface RecipesRepository {
    suspend fun loadArea(): LoadAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// implementa l'interfaccia sopra
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    override suspend fun loadArea(): LoadAreaResult {
        return recipeAPI.loadArea()
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}

// lista di ricette: lista di oggetti con nome, immagine e id
data class Recipe(val name: String, val image: String, val idMeal: Long)
data class RecipeByArea(val nameArea: String, val recipeByArea: List<Recipe>)
