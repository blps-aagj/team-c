package com.ivanmorgillo.corsoandroid.teamc.detail

import FavouriteRepository
import LoadRecipesDetailResult
import RecipeDetail
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetailScreenStates.Error.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Screens
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeDetailViewModel(
    private val recipeDetailRepository: RecipesDetailsRepository,
    private val favouriteRepository: FavouriteRepository,
    private val tracking: Tracking
) : ViewModel() {

    val states = MutableLiveData<RecipeDetailScreenStates>()
    private var recipeId = 0L

    init {
        tracking.logScreen(Screens.Details)
    }

    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> loadRecipeDetailContent(recipeId)
            RecipeDetailScreenEvent.OnErrorRandomClick -> {
                tracking.logEvent("error_random_clicked")
                loadRecipeDetailRandomContent()
            }
            is RecipeDetailScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("on_favourite_clicked")
                saveFavourite(event)
            }
        }.exhaustive
    }

    private fun saveFavourite(event: RecipeDetailScreenEvent.OnFavouriteClicked) {
        TODO()
    }

    private fun loadRecipeDetailRandomContent() {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            when (val result = recipeDetailRepository.loadDetailsRecipesRandom()) {
                is LoadRecipesDetailResult.Failure -> states.postValue(NoRecipeFound)
                is LoadRecipesDetailResult.Success -> recipesDetailsResultSuccess(result.recipesDetail)
            }.exhaustive
        }
    }

    private fun loadRecipeDetailContent(id: Long) {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            when (val result = recipeDetailRepository.loadDetailsRecipes(id)) {
                is LoadRecipesDetailResult.Failure -> states.postValue(NoRecipeFound)
                is LoadRecipesDetailResult.Success -> recipesDetailsResultSuccess(result.recipesDetail)
            }.exhaustive
        }
    }

    private suspend fun recipesDetailsResultSuccess(recipeDetails: RecipeDetail) {
        val isFavourite = favouriteRepository.isFavourite(recipeDetails.recipeId.toLong())
        val recipesDetails: List<DetailScreenItems> = listOf(
            DetailScreenItems.Image(
                recipeDetails.recipeImage,
                isFavourite
            ),
            DetailScreenItems.TitleCategoryArea(
                recipeDetails.recipeName,
                recipeDetails.recipeCategory,
                recipeDetails.recipeArea
            ),
            DetailScreenItems.Ingredients(
                recipeDetails.recipeIngredientsAndMeasures
                    .map { ingredient ->
                        IngredientUI(name = ingredient.ingredientName, measure = ingredient.ingredientQuantity)
                    }
            ),
            DetailScreenItems.Instructions(
                recipeDetails.recipeInstructions
            ),
            DetailScreenItems.VideoInstructions(
                recipeDetails.recipeVideoInstructions
            )
        )
        states.postValue(
            RecipeDetailScreenStates.Content(recipesDetails)
        )
    }

    fun setRecipeId(recipeId: Long) {
        this.recipeId = recipeId
    }
}

sealed class RecipeDetailScreenEvent {
    object OnScreenRecipeDetailReady : RecipeDetailScreenEvent()
    object OnErrorRandomClick : RecipeDetailScreenEvent()
    data class OnFavouriteClicked(val recipe: DetailScreenItems) : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    sealed class Error : RecipeDetailScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipeDetail: List<DetailScreenItems>) : RecipeDetailScreenStates()
}
