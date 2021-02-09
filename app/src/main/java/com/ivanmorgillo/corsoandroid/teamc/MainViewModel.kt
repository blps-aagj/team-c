package com.ivanmorgillo.corsoandroid.teamc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult
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
                    when (val result = repository.loadRecipes()) {
                        is LoadRecipesResult.Failure -> {
                            when (result.error) {
                                LoadRecipesError.NoInternet -> TODO()
                                LoadRecipesError.NoRecipeFound -> {
                                    actions.postValue(MainScreenAction.ShowNoInternetMessage)
                                }
                                LoadRecipesError.ServerError -> TODO()
                                LoadRecipesError.SlowInternet -> TODO()
                            }
                        }
                        is LoadRecipesResult.Success -> {
                            val recipes = result.recipes.map {
                                RecipeUI(
                                    recipeName = it.name,
                                    recipeImageUrl = it.image
                                )
                            }
                            states.postValue(MainScreenStates.Content(recipes))
                        }
                    }

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
    object ShowNoInternetMessage : MainScreenAction()
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
