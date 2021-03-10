package com.blps.aagj.cookbook.domain.detail

import LoadRecipesDetailResult

interface RecipeDetailAPI {
    suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult
    suspend fun loadDetailsRecipeRandom(): LoadRecipesDetailResult
}
