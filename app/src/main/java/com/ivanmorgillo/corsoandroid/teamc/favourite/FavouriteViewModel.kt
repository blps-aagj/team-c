package com.ivanmorgillo.corsoandroid.teamc.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenAction.NavigateToDetailFromFavourite
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenEvents.OnFavouriteScreenReady
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenContent
import com.ivanmorgillo.corsoandroid.teamc.favourite.FavouriteScreenStates.FavouriteScreenLoading
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent

// Aggiungere DB
class FavouriteViewModel(private val tracking: Tracking) : ViewModel() {

    val favouriteStates = MutableLiveData<FavouriteScreenStates>()
    private val favouriteActions = SingleLiveEvent<FavouriteScreenAction>()

    fun send(event: FavouriteScreenEvents) {
        when (event) {
            is OnFavouriteRecipeClick -> {
                tracking.logEvent("favourite_recipe_clicked")
                favouriteActions.postValue(NavigateToDetailFromFavourite(event.favouriteRecipe))
            }
            OnFavouriteScreenReady -> {
                loadContent()
            }
            is FavouriteScreenEvents.OnItemSwiped -> TODO()
        }.exhaustive
    }

    private fun loadContent() {
        favouriteStates.postValue(FavouriteScreenLoading)
        favouriteStates.postValue(FavouriteScreenContent(getDummyData()))
    }

    private fun getDummyData(): List<FavouriteRecipeUI> {
        return listOf(
            FavouriteRecipeUI(
                idRecipe = 52772,
                titleRecipe = "Teriyaki Chicken Casserole",
                imageRecipe = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg"
            ),
            FavouriteRecipeUI(
                idRecipe = 52772,
                titleRecipe = "Teriyaki Chicken Casserole",
                imageRecipe = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg"
            ),
            FavouriteRecipeUI(
                idRecipe = 52772,
                titleRecipe = "Teriyaki Chicken Casserole",
                imageRecipe = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg"
            ),
            FavouriteRecipeUI(
                idRecipe = 52772,
                titleRecipe = "Teriyaki Chicken Casserole",
                imageRecipe = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg"
            ),
        )
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
