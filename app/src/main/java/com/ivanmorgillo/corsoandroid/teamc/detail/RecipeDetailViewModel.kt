package com.ivanmorgillo.corsoandroid.teamc.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.CategoryArea
import com.ivanmorgillo.corsoandroid.teamc.detail.network.LoadRecipesDetailResult
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeDetailViewModel(private val recipeDetailRepository: RecipesDetailsRepository) : ViewModel() {

    val states = MutableLiveData<RecipeDetailScreenStates>()
    private var recipeId = 0L
    fun send(event: RecipeDetailScreenEvent) {
        Timber.d("send ViewModelDetail")
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> {
                loadRecipeDetailContent(recipeId)
            }
        }.exhaustive
    }

    private fun loadRecipeDetailContent(id: Long) {
        states.postValue(RecipeDetailScreenStates.Loading)
        viewModelScope.launch {
            when (val result = recipeDetailRepository.loadDetailsRecipes(id)) {
                is LoadRecipesDetailResult.Failure -> {
                }
                is LoadRecipesDetailResult.Success -> {
                    val recipesDetails: List<DetailScreenItems> = listOf(
                        DetailScreenItems.Title(result.recipesDetail.recipeName),
                        CategoryArea(result.recipesDetail.recipeCategory, result.recipesDetail.recipeArea),
                        DetailScreenItems.ImageIngredients(result.recipesDetail.recipeImage, listOf(
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                            IngredientUI("Milk", "500ml"),
                        )))

                    states.postValue(RecipeDetailScreenStates.Content(recipesDetails))
                }
            }.exhaustive
        }
    }

    fun setRecipeId(recipeId: Long) {
        this.recipeId = recipeId
    }
}

sealed class RecipeDetailScreenEvent {
    object OnScreenRecipeDetailReady : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    object Error : RecipeDetailScreenStates()
    data class Content(val recipeDetail: List<DetailScreenItems>) : RecipeDetailScreenStates()
}
