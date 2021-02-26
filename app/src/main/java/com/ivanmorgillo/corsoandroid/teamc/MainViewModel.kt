package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
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
                loadContent(false)
            }
            is MainScreenEvent.OnRecipeClick -> {
                // Tracking click on recipe
                tracking.logEvent("recipe_clicked")
                actions.postValue(NavigateToDetail(event.recipe))
            }
            MainScreenEvent.OnRefreshClick -> {
                // Tracking refresh
                tracking.logEvent("Refresh requested")
                loadContent(true)
            }
        }.exhaustive
    }

    private fun loadContent(forced: Boolean) {
        states.postValue(MainScreenStates.Loading) // visualizziamo progressbar mentre carica lista
        viewModelScope.launch {
            val result = repository.loadAllRecipesByArea(forced)
            if (result.isNullOrEmpty()) {
                states.postValue(MainScreenStates.Error)
            } else {
                val recipes = result.map { recipeByArea ->
                    RecipeByAreaUI(
                        nameArea = recipeByArea.nameArea,
                        recipeByArea = recipeByArea.recipeByArea.map { recipe ->
                            RecipeUI(id = recipe.idMeal, recipeName = recipe.name, recipeImageUrl = recipe.image)
                        }
                    )
                }
                states.postValue(MainScreenStates.Content(recipes))
            }
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
