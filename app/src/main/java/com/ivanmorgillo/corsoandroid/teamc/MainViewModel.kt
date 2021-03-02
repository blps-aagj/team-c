package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RecipesRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
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
        states.postValue(MainScreenStates.Loading)
        viewModelScope.launch {
            when (val result = repository.loadAllRecipesByArea(forced)) {
                is AllRecipesByAreaResult.Failure -> states.postValue(MainScreenStates.Error)
                is AllRecipesByAreaResult.Success -> {
                    val recipes = successRecipeByArea(result)
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }
        }
    }

    private fun successRecipeByArea(result: AllRecipesByAreaResult.Success): List<RecipeByAreaUI> {
        return result.contentListRecipes.map {
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
    }
}

data class RecipeByAreaUI(val nameArea: String, val recipeByArea: List<RecipeUI>)

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
}

sealed class MainScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()
    object OnReady : MainScreenEvent()
    object OnRefreshClick : MainScreenEvent()
}

sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object Error : MainScreenStates()
    data class Content(val recipes: List<RecipeByAreaUI>) : MainScreenStates()
}
