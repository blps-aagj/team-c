package com.blps.aagj.cookbook.domain.home

interface RecipeAPI {
    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipesByArea(area: String): LoadRecipesResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadAllRecipesByArea(): LoadRecipesByAreaResult
    suspend fun loadRecipeSearchByName(name: String): LoadRecipeSearchByNameResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipeByCategories(category: String): LoadRecipesResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadAllRecipesByCategory(): LoadRecipesByCategoryResult

    suspend fun loadRecipesByIngredient(name: String): LoadRecipeSearchByNameResult
}
