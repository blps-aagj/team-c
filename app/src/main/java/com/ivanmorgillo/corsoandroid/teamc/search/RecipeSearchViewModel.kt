package com.ivanmorgillo.corsoandroid.teamc.search

import FavouriteRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import kotlinx.coroutines.launch

class RecipeSearchViewModel(private val repository: RecipesRepository, private val favouriteRepository: FavouriteRepository) : ViewModel() {
    val states = MutableLiveData<RecipeSearchScreenStates>()

    fun send(event: RecipeSearchScreenEvent) {
        when (event) {
            RecipeSearchScreenEvent.OnError -> TODO()
            RecipeSearchScreenEvent.OnReady -> loadContent()

        }
    }

    private fun loadContent() {
        viewModelScope.launch {
            val result = repository.loadRecipesSearchByName("cake")
            when (result) {
                is LoadRecipeSearchByNameResult.Failure -> states.postValue(RecipeSearchScreenStates.Error.NoNetwork)
                is LoadRecipeSearchByNameResult.Success -> {
                    val recipes = result.content.map {
                        RecipeUI(
                            id = it.idMeal,
                            recipeName = it.name,
                            recipeImageUrl = it.image,
                            isFavourite = favouriteRepository.isFavourite(it.idMeal)
                        )
                    }
                    states.postValue(RecipeSearchScreenStates.Content(recipes))
                }
            }
        }
    }
}
