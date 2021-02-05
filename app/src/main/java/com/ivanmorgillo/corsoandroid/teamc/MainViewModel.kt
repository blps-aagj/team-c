package com.ivanmorgillo.corsoandroid.teamc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val MAXRANGE = 10 // costante per potere togliere il problema del magic number

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {

    private val recipeName = "Beef and Mustard pie"
    private val recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
    private val recipeList = (1..MAXRANGE).map {
        RecipeUI(
            recipeName = recipeName + it,
            recipeImageUrl = recipeImageUrl
        )
    }
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnReady -> {
                states.postValue(MainScreenStates.Content(recipeList))
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
