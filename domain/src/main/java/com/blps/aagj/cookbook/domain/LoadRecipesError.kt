package com.ivanmorgillo.corsoandroid.teamc.network

import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe

sealed class LoadRecipesError {
    object NoRecipeFound : LoadRecipesError()
    object NoInternet : LoadRecipesError()
    object GenericError : LoadRecipesError()
}

sealed class LoadRecipesResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipesResult()
    data class Failure(val error: LoadRecipesError) : LoadRecipesResult()
}
