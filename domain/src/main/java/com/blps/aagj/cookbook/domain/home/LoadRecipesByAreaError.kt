package com.blps.aagj.cookbook.domain.home

sealed class LoadRecipesByAreaError {
    object NoInternetByArea : LoadRecipesByAreaError()
    object GenericErrorByArea : LoadRecipesByAreaError()
}
