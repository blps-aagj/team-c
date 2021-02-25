package com.ivanmorgillo.corsoandroid.teamc.home

import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPI

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
