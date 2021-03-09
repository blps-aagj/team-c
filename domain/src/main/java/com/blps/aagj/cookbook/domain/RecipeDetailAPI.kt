package com.blps.aagj.cookbook.domain

import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeDetail

interface RecipeDetailAPI {
    suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult

    suspend fun loadDetailsRecipeRandom(): LoadRecipesDetailResult
}

// Gestisce il caso di un qualsiasi errore
sealed class LoadRecipesDetailError {
    object NoRecipeDetailFound : LoadRecipesDetailError()
    object NoInternet : LoadRecipesDetailError()
}

// Gestisce i due casi possibili del load
sealed class LoadRecipesDetailResult {
    data class Success(val recipesDetail: RecipeDetail) : LoadRecipesDetailResult()
    data class Failure(val error: LoadRecipesDetailError) : LoadRecipesDetailResult()
}
