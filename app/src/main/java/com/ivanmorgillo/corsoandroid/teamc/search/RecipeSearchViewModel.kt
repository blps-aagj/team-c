package com.ivanmorgillo.corsoandroid.teamc.search

import FavouriteRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import kotlinx.coroutines.launch

class RecipeSearchViewModel(private val repository: RecipesRepository, private val favouriteRepository: FavouriteRepository) : ViewModel() {
    val states = MutableLiveData<RecipeSearchScreenStates>()
    fun send(event: RecipeSearchScreenEvent) {
        when (event) {
            RecipeSearchScreenEvent.OnError -> TODO()
            RecipeSearchScreenEvent.OnReady -> states.postValue(RecipeSearchScreenStates.BlankContent)
            is RecipeSearchScreenEvent.OnRecipeSearch -> {
                loadContent(event.name)
            }
        }.exhaustive
    }

    private fun loadContent(name: String) {
        viewModelScope.launch {
            val result = repository.loadRecipesSearchByName(name)
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

sealed class RecipeSearchScreenEvent {
    object OnReady : RecipeSearchScreenEvent()
    object OnError : RecipeSearchScreenEvent()
    data class OnRecipeSearch(val name: String) : RecipeSearchScreenEvent()
}

sealed class RecipeSearchScreenStates {
    object Loading : RecipeSearchScreenStates()
    sealed class Error : RecipeSearchScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }
    object BlankContent : RecipeSearchScreenStates()
    data class Content(val recipe: List<RecipeUI>) : RecipeSearchScreenStates()
}
