package com.ivanmorgillo.corsoandroid.teamc

import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.RecipeAPI

interface RecipesRepository {
    suspend fun loadRecipes(): LoadRecipesResult
}

// implementa l'interfaccia sopra
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    override suspend fun loadRecipes(): LoadRecipesResult {
        return recipeAPI.loadRecipes()
    }
}

// lista di ricette: lista di oggetti con nome, immagine e id
data class Recipe(val name: String, val image: String, val idMeal: Long)
