package com.blps.aagj.cookbook.domain.home

sealed class LoadRecipesByCategoryError {
    object NoInternetByCategory : LoadRecipesByCategoryError()
    object GenericErrorByArea : LoadRecipesByCategoryError()
}
