package com.blps.aagj.cookbook.networking.home

sealed class LoadRecipesByCategoryError {
    object NoInternet : LoadRecipesByCategoryError()
    object NoInternetByCategory : LoadRecipesByCategoryError()
    object GenericErrorByArea : LoadRecipesByCategoryError()
}
