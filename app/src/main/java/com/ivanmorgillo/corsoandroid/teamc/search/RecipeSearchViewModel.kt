package com.ivanmorgillo.corsoandroid.teamc.search

import FavouriteRepository
import LoadRecipesDetailResult
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.detail.RecipeDetail
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Screens
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("IMPLICIT_CAST_TO_ANY")
class RecipeSearchViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val detailsRepository: RecipesDetailsRepository,
    private val tracking: Tracking,
) : ViewModel() {
    init {
        tracking.logScreen(Screens.Search)
    }

    val actions = SingleLiveEvent<RecipeSearchScreenAction>()
    val states = MutableLiveData<RecipeSearchScreenStates>()
    fun send(event: RecipeSearchScreenEvent) {
        when (event) {
            is RecipeSearchScreenEvent.OnSearchButtonClick -> {
                tracking.logEvent("search_btn_click")
                loadContent(event.searchedRecipeName)
                loadContentByIngredient(event.searchIngredientRecipe)
            }
            is RecipeSearchScreenEvent.OnRecipeClickSearched -> {
                tracking.logEvent("search_recipe_clicked")
                actions.postValue(RecipeSearchScreenAction.NavigateToDetailFromSearch(event.recipe))
            }
            is RecipeSearchScreenEvent.OnSearchKeyboardClick -> {
                tracking.logEvent("search_keyboard_clicked")
                loadContent(event.searchedRecipeName)
            }
            RecipeSearchScreenEvent.OnErrorRandomClick -> {
                tracking.logEvent("search_random_btn_no_recipe_clicked")
                loadDetailRandomRecipe()
            }
            is RecipeSearchScreenEvent.OnSearchIngredientButtonClick -> {
                tracking.logEvent("on_search_ingredient_btn")
                loadContentByIngredient(event.ingredient)
            }
            is RecipeSearchScreenEvent.OnSearchByRecipeNameButtonClick -> {
                tracking.logEvent("on_search_by_name_btn")
                loadContent(event.recipeName)
            }
        }.exhaustive
    }

    private fun loadContentByIngredient(ingredient: String) {
        states.postValue(RecipeSearchScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadRecipeByIngredient(ingredient)
            when (result) {
                is LoadRecipeSearchByNameResult.Failure -> {
                    Timber.d("RecipeIngredient failure")
                    states.postValue(RecipeSearchScreenStates.Error.NoNetwork)
                }
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
            }.exhaustive
        }
    }

    private fun loadDetailRandomRecipe(): Job {
        return viewModelScope.launch {
            when (val result = detailsRepository.loadDetailsRecipesRandom()) {
                is LoadRecipesDetailResult.Failure -> {
                    Timber.d("RecipeId failure")
                    states.postValue(RecipeSearchScreenStates.Error.NoNetwork)
                }
                is LoadRecipesDetailResult.Success -> {
                    Timber.d("RecipeId passed")
                    val recipeDetail = result.recipesDetail
                    actions.postValue(RecipeSearchScreenAction.NavigateToDetailRandom(recipeDetail))
                }
            }.exhaustive
        }
    }

    private fun loadContent(name: String) {
        states.postValue(RecipeSearchScreenStates.Loading)
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
}

sealed class RecipeSearchScreenAction {
    data class NavigateToDetailFromSearch(val recipe: RecipeUI) : RecipeSearchScreenAction()
    class NavigateToDetailRandom(val recipeDetail: RecipeDetail) : RecipeSearchScreenAction()
}

sealed class RecipeSearchScreenEvent {
    data class OnRecipeClickSearched(val recipe: RecipeUI) : RecipeSearchScreenEvent()
    data class OnSearchButtonClick(val searchedRecipeName: String, val searchIngredientRecipe: String) : RecipeSearchScreenEvent()
    data class OnSearchKeyboardClick(val searchedRecipeName: String) : RecipeSearchScreenEvent()
    object OnErrorRandomClick : RecipeSearchScreenEvent()
    data class OnSearchIngredientButtonClick(val ingredient: String) : RecipeSearchScreenEvent()
    data class OnSearchByRecipeNameButtonClick(val recipeName: String) : RecipeSearchScreenEvent()
}

sealed class RecipeSearchScreenStates {
    object Loading : RecipeSearchScreenStates()
    sealed class Error : RecipeSearchScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipe: List<RecipeUI>) : RecipeSearchScreenStates()
}
