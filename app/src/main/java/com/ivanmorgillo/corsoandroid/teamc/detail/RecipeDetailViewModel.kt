package com.ivanmorgillo.corsoandroid.teamc.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.detail.network.LoadRecipesDetailResult
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeDetailViewModel(private val recipeDetailRepository: RecipesDetailsRepository) : ViewModel() {
    val states = MutableLiveData<RecipeDetailScreenStates>()
    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> {
                loadRecipeDetailContent()
            }
        }.exhaustive
    }

    private fun loadRecipeDetailContent() {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {

            when (val result = recipeDetailRepository.loadDetailsRecipes(id = 52773)) {
                is LoadRecipesDetailResult.Failure -> {
                }
                is LoadRecipesDetailResult.Success -> {
                    val recipesDetails = result.recipesDetail.map {
                        RecipeDetail(
                            recipeName = it.recipeName,
                            recipeCategory = it.recipeCategory,
                            recipeArea = it.recipeArea,
                            recipeInstructions = listOf(), // da implementare
                            recipeImage = it.recipeImage,
                            recipeIngredientsAndMeasures = listOf(), // da implementare
                            recipeVideoInstructions = it.recipeVideoInstructions
                        )
                    }
                    states.postValue(RecipeDetailScreenStates.Content(recipesDetails))
                }
            }.exhaustive
        }
    }
}

sealed class RecipeDetailScreenEvent {
    object OnScreenRecipeDetailReady : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    object Error : RecipeDetailScreenStates()
    data class Content(val recipeDetail: List<RecipeDetail>) : RecipeDetailScreenStates()
}
