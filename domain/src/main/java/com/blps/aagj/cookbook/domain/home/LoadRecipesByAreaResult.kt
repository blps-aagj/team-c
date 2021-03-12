package com.blps.aagj.cookbook.domain.home

import RecipeByArea

sealed class LoadRecipesByAreaResult {
    data class Failure(val error: LoadRecipesByAreaError) : LoadRecipesByAreaResult()
    data class Success(val contentListRecipes: List<RecipeByArea>) : LoadRecipesByAreaResult()
}
