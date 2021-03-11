package com.ivanmorgillo.corsoandroid.teamc

import FavouriteRepository
import LoadRecipesDetailResult
import RecipeByArea
import RecipeDetail
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val detailsRepository: RecipesDetailsRepository,
    private val tracking: Tracking
) : ViewModel() {

    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()
    private var recipes: List<RecipeByArea>? = null
    private var recipesByAreaUI: List<RecipeByAreaUI>? = null

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnReady -> loadContent(false)
            is MainScreenEvent.OnRecipeClick -> {
                tracking.logEvent("home_recipe_clicked")
                actions.postValue(NavigateToDetail(event.recipe))
            }
            MainScreenEvent.OnRefreshClick -> {
                tracking.logEvent("home_refresh_clicked")
                loadContent(true)
            }
            is MainScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("home_favorite_clicked")
                saveFavourite(event.recipe)
            }
            is MainScreenEvent.OnRandomClick -> {
                tracking.logEvent("home_random_clicked")
                loadDetailRandomRecipe()
            }
            MainScreenEvent.OnFeedbackClicked -> {
                tracking.logEvent("drawer_feedback_clicked")
            }
            MainScreenEvent.OnFavouriteListMenuClicked -> {
                tracking.logEvent("drawer_favourite_list_clicked")
            }
        }.exhaustive
    }

    private fun saveFavourite(clickedRecipe: RecipeUI): Job {
        val isFavourite = !clickedRecipe.isFavourite
        return viewModelScope.launch {
            recipes
                ?.map {
                    it.recipeByArea
                }
                ?.flatten()
                ?.find {
                    clickedRecipe.id == it.idMeal
                }
                ?.run {
                    favouriteRepository.save(this, isFavourite)
                }
            val updatedRecipe = clickedRecipe.copy(isFavourite = isFavourite)
            updateContent(updatedRecipe)
        }
    }

    private fun updateContent(clickedRecipe: RecipeUI) {
        recipesByAreaUI
            ?.asSequence()
            ?.map { recipeByAreaUI ->
                updateRecipeByAreaUI(recipeByAreaUI, clickedRecipe)
            }
            ?.toList()
            ?.run {
                recipesByAreaUI = this
                val content = MainScreenStates.Content(this)
                states.postValue(content)
            }
    }

    private fun updateRecipeByAreaUI(
        recipeByAreaUI: RecipeByAreaUI,
        clickedRecipe: RecipeUI
    ): RecipeByAreaUI {
        var clickedRecipePosition = 0
        val recipes = recipeByAreaUI.recipeByArea
            .asSequence()
            .mapIndexed { index, recipeUI ->
                if (recipeUI.id == clickedRecipe.id) {
                    clickedRecipePosition = index
                    clickedRecipe
                } else {
                    recipeUI
                }
            }
        return recipeByAreaUI.copy(
            recipeByArea = recipes.toList(),
            selectedRecipePosition = clickedRecipePosition,
        )
    }

    private fun loadDetailRandomRecipe(): Job {
        states.postValue(MainScreenStates.Loading)
        return viewModelScope.launch {
            when (val result = detailsRepository.loadDetailsRecipesRandom()) {
                is LoadRecipesDetailResult.Failure -> {
                    Timber.d("RecipeId failure")
                    states.postValue(MainScreenStates.Error.NoNetwork)
                }
                is LoadRecipesDetailResult.Success -> {
                    Timber.d("RecipeId passed")
                    val recipeDetail = result.recipesDetail
                    actions.postValue(MainScreenAction.NavigateToDetailRandom(recipeDetail))
                }
            }.exhaustive
        }
    }

    private fun loadContent(forced: Boolean) {
        states.postValue(MainScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAllRecipesByArea(forced)
            when (result) {
                is LoadRecipesByAreaResult.Failure -> states.postValue(MainScreenStates.Error.NoNetwork)
                is LoadRecipesByAreaResult.Success -> {
                    recipes = result.contentListRecipes
                    val recipes = result.contentListRecipes
                        .map {
                            RecipeByAreaUI(
                                nameArea = it.nameArea,
                                recipeByArea = it.recipeByArea.map { recipe ->
                                    RecipeUI(
                                        id = recipe.idMeal,
                                        recipeName = recipe.name,
                                        recipeImageUrl = recipe.image,
                                        isFavourite = favouriteRepository.isFavourite(recipe.idMeal)
                                    )
                                },
                                selectedRecipePosition = 0
                            )
                        }
                    recipesByAreaUI = recipes
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }
        }
    }
}

data class RecipeByAreaUI(val nameArea: String, val recipeByArea: List<RecipeUI>, val selectedRecipePosition: Int)

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
    data class NavigateToDetailRandom(val recipe: RecipeDetail) : MainScreenAction()
}

sealed class MainScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()
    data class OnFavouriteClicked(val recipe: RecipeUI) : MainScreenEvent()
    object OnRandomClick : MainScreenEvent()

    object OnFavouriteListMenuClicked : MainScreenEvent()
    object OnFeedbackClicked : MainScreenEvent()
    object OnReady : MainScreenEvent()
    object OnRefreshClick : MainScreenEvent()
}

sealed class MainScreenStates {
    object Loading : MainScreenStates()

    sealed class Error : MainScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipes: List<RecipeByAreaUI>) : MainScreenStates()
}
