package com.blps.aagj.cookbook.domain

import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult

interface RecipeAPI {
    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipes(area: String): LoadRecipesResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadAllRecipesByArea(): AllRecipesByAreaResult
}
