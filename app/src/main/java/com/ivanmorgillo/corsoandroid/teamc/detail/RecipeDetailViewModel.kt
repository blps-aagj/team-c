package com.ivanmorgillo.corsoandroid.teamc.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetailScreenStates.Error.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.network.detail.LoadRecipesDetailResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.detail.LoadRecipesDetailResult.Success
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeDetailViewModel(private val recipeDetailRepository: RecipesDetailsRepository, private val tracking: Tracking) : ViewModel() {

    val states = MutableLiveData<RecipeDetailScreenStates>()
    private var recipeId = 0L
    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> loadRecipeDetailContent(recipeId)
            RecipeDetailScreenEvent.OnErrorRandomClick -> {
                tracking.logEvent("error_random_clicked")
                loadRecipeDetailRandomContent()
            }
        }.exhaustive
    }

    private fun loadRecipeDetailRandomContent() {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            when (val result = recipeDetailRepository.loadDetailsRecipesRandom()) {
                is Failure -> states.postValue(NoRecipeFound)
                is Success -> recipesDetailsResultSuccess(result)
            }.exhaustive
        }
    }

    private fun loadRecipeDetailContent(id: Long) {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            when (val result = recipeDetailRepository.loadDetailsRecipes(id)) {
                is Failure -> states.postValue(NoRecipeFound)
                is Success -> recipesDetailsResultSuccess(result)
            }.exhaustive
        }
    }

    private fun recipesDetailsResultSuccess(result: Success) {
        val recipesDetails: List<DetailScreenItems> = listOf(
            DetailScreenItems.Image(
                result.recipesDetail.recipeImage,
            ),
            DetailScreenItems.TitleCategoryArea(
                result.recipesDetail.recipeName,
                result.recipesDetail.recipeCategory,
                result.recipesDetail.recipeArea
            ),
            DetailScreenItems.Ingredients(
                result.recipesDetail.recipeIngredientsAndMeasures
                    .map { ingredient ->
                        IngredientUI(name = ingredient.ingredientName, measure = ingredient.ingredientQuantity)
                    }
            ),
            DetailScreenItems.Instructions(
                result.recipesDetail.recipeInstructions
            ),
            DetailScreenItems.VideoInstructions(
                result.recipesDetail.recipeVideoInstructions
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
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    sealed class Error : RecipeDetailScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipeDetail: List<DetailScreenItems>) : RecipeDetailScreenStates()
}
