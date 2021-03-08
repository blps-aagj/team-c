package com.ivanmorgillo.corsoandroid.teamc.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenAction.NavigateToDetailFromFavourite
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteScreenReady
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenContent
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenLoading
import com.ivanmorgillo.corsoandroid.teamc.firebase.Screens
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.launch

// Aggiungere DB

class FavouriteViewModel(
    private val repository: FavouriteRepository,
    private val tracking: Tracking
) : ViewModel() {

    private var recipes: List<Recipe>? = null
    val favouriteStates = MutableLiveData<FavouriteScreenStates>()
    val favouriteActions = SingleLiveEvent<FavouriteScreenAction>()

    init {
        tracking.logScreen(Screens.Favourites)
    }

    fun send(event: FavouriteScreenEvents) {
        when (event) {
            is OnFavouriteRecipeClick -> onFavouriteRecipeClick(event)
            OnFavouriteScreenReady -> onFavouriteScreenReady()
            is FavouriteScreenEvents.OnItemSwiped -> onItemSwiped(event.position)
        }.exhaustive
    }

    private fun onItemSwiped(position: Int) {
        tracking.logEvent("favourite_recipe_swiped")
        val recipeToDelete = recipes?.get(position) ?: return
        viewModelScope.launch {
            repository.delete(recipeToDelete.idMeal)
            loadContent()
        }
    }

    private fun onFavouriteScreenReady() {
        viewModelScope.launch {
            loadContent()
        }
    }

    private fun onFavouriteRecipeClick(event: OnFavouriteRecipeClick) {
        tracking.logEvent("favourite_recipe_clicked")
        favouriteActions.postValue(NavigateToDetailFromFavourite(event.favouriteRecipe))
    }

    private suspend fun loadContent() {
        favouriteStates.postValue(FavouriteScreenLoading)
        val recipes = repository.loadAll()
        this.recipes = recipes
        val favouriteUiList = recipes.map {
            FavouriteRecipeUI(
                idRecipe = it.idMeal,
                titleRecipe = it.name,
                imageRecipe = it.image,
            )
        }
        favouriteStates.postValue(FavouriteScreenContent(favouriteUiList))
    }
}

sealed class FavouriteScreenAction {
    data class NavigateToDetailFromFavourite(val recipe: FavouriteRecipeUI) : FavouriteScreenAction()
}

sealed class FavouriteScreenEvents {
    data class OnFavouriteRecipeClick(val favouriteRecipe: FavouriteRecipeUI) : FavouriteScreenEvents()
    data class OnItemSwiped(val position: Int) : FavouriteScreenEvents()
    object OnFavouriteScreenReady : FavouriteScreenEvents()
}

sealed class FavouriteScreenStates {
    object FavouriteScreenLoading : FavouriteScreenStates()
    object FavouriteScreenError : FavouriteScreenStates()
    data class FavouriteScreenContent(val favouriteUiList: List<FavouriteRecipeUI>) : FavouriteScreenStates()
}
