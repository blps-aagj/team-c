package com.ivanmorgillo.corsoandroid.teamc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(val repository: RecipesRepository) : ViewModel() {

    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
            // activity pronta
            MainScreenEvent.OnReady -> {
                states.postValue(MainScreenStates.Loading) // visualizziamo progressbar mentre carica lista
                viewModelScope.launch {
                    val recipes = repository.loadRecipes().map {
                        RecipeUI(
                            recipeName = it.name,
                            recipeImageUrl = it.image
                        )
                    }
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }
            is MainScreenEvent.OnRecipeClick -> {
                Log.d("RECIPE", event.recipe.toString())
                actions.postValue(MainScreenAction.NavigateToDetail(event.recipe))
            }
        }
    }
}

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
}

sealed class MainScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()

    object OnReady : MainScreenEvent()
}

// Stati che rappresentano la nostra schermata
sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object Error : MainScreenStates()
    data class Content(val recipes: List<RecipeUI>) : MainScreenStates()
}
