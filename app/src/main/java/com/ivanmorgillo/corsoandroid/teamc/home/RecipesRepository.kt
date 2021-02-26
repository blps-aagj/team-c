package com.ivanmorgillo.corsoandroid.teamc.home

import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPI

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): LoadAreaResult
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// implementa l'interfaccia sopra
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    var loadAreaResult: LoadAreaResult? = null
    override suspend fun loadAllRecipesByArea(forced: Boolean): LoadAreaResult {
        if (!forced) {
            return if (loadAreaResult == null) {
                val loadArea = recipeAPI.loadArea()
                loadAreaResult = loadArea
                loadArea
            } else {
                loadAreaResult!!
            }
        }
        return recipeAPI.loadArea()
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}
