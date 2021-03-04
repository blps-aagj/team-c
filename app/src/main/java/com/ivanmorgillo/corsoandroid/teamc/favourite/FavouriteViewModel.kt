package com.ivanmorgillo.corsoandroid.teamc.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenAction.NavigateToDetailFromFavourite
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteScreenReady
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenContent
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenLoading
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.launch

// Aggiungere DB
class FavouriteViewModel(
    private val repository: FavouriteRepository,
    private val tracking: Tracking
) : ViewModel() {

    val favouriteStates = MutableLiveData<FavouriteScreenStates>()
    private val favouriteActions = SingleLiveEvent<FavouriteScreenAction>()

    fun send(event: FavouriteScreenEvents) {
        when (event) {
            is OnFavouriteRecipeClick -> {
                tracking.logEvent("favourite_recipe_clicked")
                favouriteActions.postValue(NavigateToDetailFromFavourite(event.favouriteRecipe))
            }
            OnFavouriteScreenReady -> {
                viewModelScope.launch {
                    loadContent()
                }
            }
            is FavouriteScreenEvents.OnItemSwiped -> TODO()
        }.exhaustive
    }

    private suspend fun loadContent() {
        favouriteStates.postValue(FavouriteScreenLoading)
        val favouriteUiList = repository.loadFavourites().map {
            FavouriteRecipeUI(
                idRecipe = it.id,
                titleRecipe = it.recipeName,
                imageRecipe = it.recipeImageUrl,
            )
        }
        favouriteStates.postValue(FavouriteScreenContent(favouriteUiList))
    }
}

interface FavouriteRepository {
    suspend fun loadFavourites(): List<RecipeUI>
    suspend fun save(recipe: RecipeUI, isFavourite: Boolean)
}

class FavouriteRepositoryImpl : FavouriteRepository {
    private val favouriteListID: MutableList<RecipeUI> = mutableListOf()
    override suspend fun loadFavourites(): List<RecipeUI> {
        return favouriteListID
    }

    override suspend fun save(recipe: RecipeUI, isFavourite: Boolean) {
        if (isFavourite) {
            favouriteListID.add(recipe)
        } else {
            favouriteListID.remove(recipe)
        }
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
