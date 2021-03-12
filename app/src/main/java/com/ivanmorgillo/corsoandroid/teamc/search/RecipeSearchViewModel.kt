package com.ivanmorgillo.corsoandroid.teamc.search

import FavouriteRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RecipeSearchViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val tracking: Tracking,
) : ViewModel() {
    val actions = SingleLiveEvent<RecipeSearchScreenAction>()
    val states = MutableLiveData<RecipeSearchScreenStates>()
    fun send(event: RecipeSearchScreenEvent) {
        when (event) {
            RecipeSearchScreenEvent.OnError -> TODO()
            is RecipeSearchScreenEvent.OnSearchButtonClick -> {
                tracking.logEvent("search_btn_click")
                loadContent(event.searchedRecipeName)
            }
            is RecipeSearchScreenEvent.OnRecipeClickSearched -> {
                tracking.logEvent("search_recipe_clicked")
                actions.postValue(RecipeSearchScreenAction.NavigateToDetailFromSearch(event.recipe))
            }
            is RecipeSearchScreenEvent.OnSearchKeyboardClick -> {
                tracking.logEvent("search_keyboard_click")
                loadContent(event.searchedRecipeName)
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

sealed class RecipeSearchScreenAction {
    data class NavigateToDetailFromSearch(val recipe: RecipeUI) : RecipeSearchScreenAction()
}

sealed class RecipeSearchScreenEvent {
    data class OnRecipeClickSearched(val recipe: RecipeUI) : RecipeSearchScreenEvent()
    data class OnSearchButtonClick(val searchedRecipeName: String) : RecipeSearchScreenEvent()
    data class OnSearchKeyboardClick(val searchedRecipeName: String) : RecipeSearchScreenEvent()
    object OnError : RecipeSearchScreenEvent()
}

sealed class RecipeSearchScreenStates {
    object Loading : RecipeSearchScreenStates()
    sealed class Error : RecipeSearchScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipe: List<RecipeUI>) : RecipeSearchScreenStates()
}
