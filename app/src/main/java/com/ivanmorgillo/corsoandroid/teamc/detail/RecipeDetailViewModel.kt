package com.ivanmorgillo.corsoandroid.teamc.detail

import FavouriteRepository
import LoadRecipesDetailResult
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.AuthenticationManager
import com.blps.aagj.cookbook.domain.detail.RecipeDetail
import com.blps.aagj.cookbook.domain.detail.toRecipe
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetailScreenStates.Error.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Screens
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeDetailViewModel(
    private val recipeDetailRepository: RecipesDetailsRepository,
    private val favouriteRepository: FavouriteRepository,
    private val tracking: Tracking,
    private val authenticationManager: AuthenticationManager
) : ViewModel() {

    val states = MutableLiveData<RecipeDetailScreenStates>()
    private var recipeDetail: RecipeDetail? = null
    private var isFavourite: Boolean = false

    init {
        tracking.logScreen(Screens.Details)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            is RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> loadRecipeDetailContent(event.recipeId)
            RecipeDetailScreenEvent.OnErrorRandomClick -> {
                tracking.logEvent("error_random_clicked")
                loadRecipeDetailRandomContent()
            }
            RecipeDetailScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("on_favourite_clicked")
                if (authenticationManager.isUserLoggedIn()) {
                    viewModelScope.launch {
                        saveFavourite()
                    }
                } else {
                    states.postValue(RecipeDetailScreenStates.NoLogged)
                }
            }
            RecipeDetailScreenEvent.OnLoginDialogClick -> {
                tracking.logEvent("on_login_dialog_click_detail")
            }
        }.exhaustive
    }

    private suspend fun saveFavourite() {
        val recipe = recipeDetail ?: return
        val updatedFavourite = !isFavourite
        if (updatedFavourite) {
            favouriteRepository.save(
                recipe = recipe.toRecipe(),
                isFavourite = updatedFavourite
            )
            isFavourite = updatedFavourite
            recipesDetailsResultSuccess(recipe)
        } else {
            favouriteRepository.delete(recipe.recipeId.toLong())
            isFavourite = updatedFavourite
            recipesDetailsResultSuccess(recipe)
        }
    }

    private fun loadRecipeDetailRandomContent() {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            val result = recipeDetailRepository.loadDetailsRecipesRandom()
            when (result) {
                is LoadRecipesDetailResult.Failure -> states.postValue(NoRecipeFound)
                is LoadRecipesDetailResult.Success -> recipesDetailsResultSuccess(result.recipesDetail)
            }.exhaustive
        }
    }

    private fun loadRecipeDetailContent(id: Long) {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            val result = recipeDetailRepository.loadDetailsRecipes(id)
            when (result) {
                is LoadRecipesDetailResult.Failure -> states.postValue(NoRecipeFound)
                is LoadRecipesDetailResult.Success -> recipesDetailsResultSuccess(result.recipesDetail)
            }.exhaustive
        }
    }

    private suspend fun recipesDetailsResultSuccess(recipeDetails: RecipeDetail) {
        recipeDetail = recipeDetails
        isFavourite = favouriteRepository.isFavourite(recipeDetails.recipeId.toLong())
        val screenItems: List<DetailScreenItems> = listOf(
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
        )
        val updatedScreenItems = if (recipeDetails.recipeVideoInstructions != null) {
            screenItems.plus(DetailScreenItems.VideoInstructions(recipeDetails.recipeVideoInstructions!!))
        } else {
            screenItems
        }
        states.postValue(
            RecipeDetailScreenStates.Content(updatedScreenItems)
        )
    }
}

sealed class RecipeDetailScreenEvent {
    data class OnScreenRecipeDetailReady(val recipeId: Long) : RecipeDetailScreenEvent()
    object OnErrorRandomClick : RecipeDetailScreenEvent()
    object OnFavouriteClicked : RecipeDetailScreenEvent()
    object OnLoginDialogClick : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    sealed class Error : RecipeDetailScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    object NoLogged : RecipeDetailScreenStates()

    data class Content(val recipeDetail: List<DetailScreenItems>) : RecipeDetailScreenStates()
}
