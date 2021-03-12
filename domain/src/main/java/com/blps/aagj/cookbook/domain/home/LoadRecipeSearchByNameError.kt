package com.blps.aagj.cookbook.domain.home

sealed class LoadRecipeSearchByNameError {
    object NoInternet : LoadRecipeSearchByNameError()
    object GenericError : LoadRecipeSearchByNameError()
}
