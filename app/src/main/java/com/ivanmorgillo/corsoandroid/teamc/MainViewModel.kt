package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.MainScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.network.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RecipesRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>() // potremmo passarci direttamente loading // mai null
    val actions = SingleLiveEvent<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
            // Activity pronta
            MainScreenEvent.OnReady -> {
                loadContent()
            }
            is MainScreenEvent.OnRecipeClick -> {
                // Tracking click on recipe
                tracking.logEvent("recipe_clicked")
                actions.postValue(NavigateToDetail(event.recipe))
            }
            MainScreenEvent.OnRefreshClick -> {
                // Tracking refresh
                tracking.logEvent("Refresh requested")
                loadContent()
            }
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(MainScreenStates.Loading) // visualizziamo progressbar mentre carica lista
        viewModelScope.launch {
            when (val result = repository.loadArea()) {
                is LoadAreaResult.Failure -> {
                    states.postValue(Error)
                    when (result.error) {
                        LoadRecipesError.NoInternet -> {
                            actions.postValue(MainScreenAction.ShowNoInternetMessage)
                        }
                        LoadRecipesError.NoRecipeFound -> TODO()
                        LoadRecipesError.ServerError -> TODO()
                        LoadRecipesError.SlowInternet -> TODO()
                    }
                }
                is LoadAreaResult.Success -> {

                    val recipes = result.recipes.map {
                        RecipeByAreaUI(
                            nameArea = it.nameArea,
                            recipeByArea = it.recipeByArea.map { recipe ->
                                RecipeUI(
                                    id = recipe.idMeal,
                                    recipeName = recipe.name,
                                    recipeImageUrl = recipe.image

                                )
                            }
                        )
                    }
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }.exhaustive
        }
    }
}

data class RecipeByAreaUI(val nameArea: String, val recipeByArea: List<RecipeUI>)

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
    object ShowNoInternetMessage : MainScreenAction()
}

sealed class MainScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()

    object OnReady : MainScreenEvent()
    object OnRefreshClick : MainScreenEvent()
}

// Stati che rappresentano la nostra schermata
sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object Error : MainScreenStates()
    data class Content(val recipes: List<RecipeByAreaUI>) : MainScreenStates()
}
