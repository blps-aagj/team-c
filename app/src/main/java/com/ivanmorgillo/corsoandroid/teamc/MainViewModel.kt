package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(val repository: RecipesRepository) : ViewModel() {

    val states = MutableLiveData<MainScreenStates>() // potremmo passarci direttamente loading // mai null
    val actions = SingleLiveEvent<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
            // activity pronta
            MainScreenEvent.OnReady -> {
                /*when (states.value ){
                    // 4 scenari
                    is MainScreenStates.Content -> TODO("NON DOVREMMO FARE NIENTE") // l'utente chiude e apre l'app
                    MainScreenStates.Error -> TODO("FORSE SIAMO TORNATI NEL WIFI E ABBIAMO BISOGNO DELLA  LOADING")
                    MainScreenStates.Loading -> TODO("L'UTENTE è USCITO DALL'APP MENTRE STAVA ANCORA IN LOADING(!?)")
                    null -> TODO("da valutare lo stato iniziale della app")
                }*/
                // aggiungere exaustive
                loadContent()
            }
            is MainScreenEvent.OnRecipeClick -> {
                //add tracking
                actions.postValue(MainScreenAction.NavigateToDetail(event.recipe))
            }
            MainScreenEvent.OnRefreshClick -> {
                // add tracking
                loadContent()
            }
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(MainScreenStates.Loading) // visualizziamo progressbar mentre carica lista
        viewModelScope.launch {
            when (val result = repository.loadRecipes()) {
                is LoadRecipesResult.Failure -> {
                    when (result.error) {
                        LoadRecipesError.NoInternet -> {
                            actions.postValue(MainScreenAction.ShowNoInternetMessage)
                        }
                        LoadRecipesError.NoRecipeFound -> TODO()
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
            }.exhaustive
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
    object OnRefreshClick : MainScreenEvent()
}

// Stati che rappresentano la nostra schermata
sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object Error : MainScreenStates()
    data class Content(val recipes: List<RecipeUI>) : MainScreenStates()
}
