package com.blps.aagj.cookbook.domain.home

import RecipeByCategory

sealed class LoadRecipesByCategoryResult {
    data class Failure(val error: LoadRecipesByCategoryError) : LoadRecipesByCategoryResult()
    data class Success(val contentListRecipesByCategory: List<RecipeByCategory>) : LoadRecipesByCategoryResult()
}
