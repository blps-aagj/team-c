package com.blps.aagj.cookbook.domain.home

import Recipe

sealed class LoadRecipeSearchByNameResult {
    data class Failure(val error: LoadRecipeSearchByNameError) : LoadRecipeSearchByNameResult()
    data class Success(val content: List<Recipe>) : LoadRecipeSearchByNameResult()
}
