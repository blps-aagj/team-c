package com.ivanmorgillo.corsoandroid.teamc.detail

import FavouriteRepository
import LoadRecipesDetailResult
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val tracking: Tracking
) : ViewModel() {

    val states = MutableLiveData<RecipeDetailScreenStates>()
    private var recipeId = 0L
    private var recipeDetail: RecipeDetail? = null
    private var isFavourite: Boolean = false

    init {
        tracking.logScreen(Screens.Details)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> loadRecipeDetailContent(recipeId)
            RecipeDetailScreenEvent.OnErrorRandomClick -> {
                tracking.logEvent("error_random_clicked")
                loadRecipeDetailRandomContent()
            }
            RecipeDetailScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("on_favourite_clicked")
                viewModelScope.launch {
                    saveFavourite()
                }
            }
        }.exhaustive
    }

    private suspend fun saveFavourite() {
        val recipe = recipeDetail ?: return
        val updatedFavourite = !isFavourite
        favouriteRepository.save(
            recipe = recipe.toRecipe(),
            isFavourite = updatedFavourite
        )
        isFavourite = updatedFavourite
        recipesDetailsResultSuccess(recipe)
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
    object OnFavouriteClicked : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    sealed class Error : RecipeDetailScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipeDetail: List<DetailScreenItems>) : RecipeDetailScreenStates()
}
