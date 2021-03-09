package com.blps.aagj.cookbook.domain.home

interface RecipeAPI {
    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipes(area: String): LoadRecipesResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadAllRecipesByArea(): LoadRecipesByAreaResult
}
