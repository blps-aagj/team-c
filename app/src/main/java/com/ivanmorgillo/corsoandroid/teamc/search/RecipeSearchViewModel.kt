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
import timber.log.Timber

class RecipeSearchViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val tracking: Tracking,
) : ViewModel() {
    val actions = SingleLiveEvent<RecipeSearchScreenAction>()
    val states = MutableLiveData<RecipeSearchScreenStates>()
    private var recipeSearchString = ""

    fun send(event: RecipeSearchScreenEvent) {
        when (event) {
            RecipeSearchScreenEvent.OnError -> TODO()
            RecipeSearchScreenEvent.OnReady -> loadContent("")
            is RecipeSearchScreenEvent.OnRecipeSearch -> {
                Timber.d("searchText$recipeSearchString")
                loadContent(recipeSearchString)
            }
            is RecipeSearchScreenEvent.OnRecipeClickSearched -> {
                tracking.logEvent("search_recipe_clicked")
                actions.postValue(RecipeSearchScreenAction.NavigateToDetailFromSearch(event.recipe))
            }
        }.exhaustive
    }

    private fun loadContent(name: String) {
        viewModelScope.launch {
            val result = repository.loadRecipesSearchByName(name)
            when (result) {
                is LoadRecipeSearchByNameResult.Failure -> states.postValue(RecipeSearchScreenStates.Error.NoRecipeFound)
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

    fun setRecipeName(recipeName: String) {
        this.recipeSearchString = recipeName
    }
}

sealed class RecipeSearchScreenAction {
    data class NavigateToDetailFromSearch(val recipe: RecipeUI) : RecipeSearchScreenAction()
}

sealed class RecipeSearchScreenEvent {
    data class OnRecipeClickSearched(val recipe: RecipeUI) : RecipeSearchScreenEvent()
    object OnReady : RecipeSearchScreenEvent()
    object OnError : RecipeSearchScreenEvent()
    data class OnRecipeSearch(private val name: String) : RecipeSearchScreenEvent()
}

sealed class RecipeSearchScreenStates {
    object Loading : RecipeSearchScreenStates()
    sealed class Error : RecipeSearchScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipe: List<RecipeUI>) : RecipeSearchScreenStates()
}
