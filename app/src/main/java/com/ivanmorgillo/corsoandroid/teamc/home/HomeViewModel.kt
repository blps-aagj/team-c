package com.ivanmorgillo.corsoandroid.teamc.home

import FavouriteRepository
import LoadRecipesDetailResult
import Recipe
import RecipeByArea
import RecipeByCategory
import RecipesDetailsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blps.aagj.cookbook.domain.AuthenticationManager
import com.blps.aagj.cookbook.domain.detail.RecipeDetail
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaResult
import com.blps.aagj.cookbook.domain.home.LoadRecipesByCategoryResult
import com.blps.aagj.cookbook.domain.home.RecipesRepository
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.home.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val detailsRepository: RecipesDetailsRepository,
    private val tracking: Tracking,
    private val authenticationManager: AuthenticationManager
) : ViewModel() {

    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()
    private var recipesByArea: List<RecipeByArea>? = null
    private var recipesByCategory: List<RecipeByCategory>? = null
    private var recipesByTabUI: List<RecipeByTabUI>? = null

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnReady -> loadContent(false)
            is MainScreenEvent.OnRecipeClick -> {
                tracking.logEvent("home_recipe_clicked")
                actions.postValue(NavigateToDetail(event.recipe))
            }
            is MainScreenEvent.OnRefreshClick -> {
                tracking.logEvent("home_refresh_clicked")
                onRefreshClick(event)
            }
            is MainScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("home_favorite_clicked")
                onFavouriteClicked(event)
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
            MainScreenEvent.OnSearchClick -> {
                tracking.logEvent("home_search_clicked")
                Timber.d("OnSearchClick")
                actions.postValue(MainScreenAction.NavigateToSearch)
            }
            MainScreenEvent.OnLoginDialogClick -> {
                tracking.logEvent("login_dialog_clicked_home")
            }
            MainScreenEvent.OnClickedCategory -> {
                tracking.logEvent("clicked_tab_category")
                loadCategoryContent(false)
            }
            MainScreenEvent.OnClickedNation -> {
                tracking.logEvent("clicked_tab_nation")
                loadContent(false)
            }
        }.exhaustive
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun onFavouriteClicked(event: MainScreenEvent.OnFavouriteClicked) =
        if (authenticationManager.isUserLoggedIn()) {
            viewModelScope.launch {
                if (!savingInProgress) {
                    saveFavourite(event.recipe)
                } else {
                    Timber.d("saving is in progress")
                }
            }
        } else {
            states.postValue(MainScreenStates.NoLogged)
        }

    private fun onRefreshClick(event: MainScreenEvent.OnRefreshClick) {
        when (event.selectedTab) {
            "Nation" -> {
                loadContent(true)
            }
            "Category" -> {
                loadCategoryContent(true)
            }
            else -> {
                Timber.d("msg terapia tapioca")
            }
        }
    }

    private fun loadCategoryContent(forced: Boolean) {
        states.postValue(MainScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAllRecipesByCategory(forced)
            when (result) {
                is LoadRecipesByCategoryResult.Failure -> {
                    states.postValue(MainScreenStates.Error.NoNetwork)
                }
                is LoadRecipesByCategoryResult.Success -> {
                    recipesByCategory = result.contentListRecipesByCategory
                    val favourites = favouriteRepository.loadAll() ?: emptyList()
                    val recipes = result.contentListRecipesByCategory
                        .map {
                            RecipeByTabUI(
                                nameTab = it.nameCategory,
                                recipeByTab = it.recipeByCategory
                                    .map { recipe ->
                                        recipe.toUI(favourites)
                                    },
                                selectedRecipePosition = 0
                            )
                        }
                    recipesByTabUI = recipes
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }.exhaustive
        }
    }

    private var savingInProgress: Boolean = false
    private suspend fun saveFavourite(clickedRecipe: RecipeUI) {
        savingInProgress = true
        val isFavourite = !clickedRecipe.isFavourite
        if (isFavourite) {
            recipesByArea
                ?.map {
                    it.recipeByArea
                }
                ?.flatten()
                ?.find {
                    clickedRecipe.id == it.idMeal
                }
                ?.run {
                    favouriteRepository.save(this, isFavourite)
                    savingInProgress = false
                }
            val updatedRecipe = clickedRecipe.copy(isFavourite = isFavourite)
            updateContent(updatedRecipe)
        } else {
            favouriteRepository.delete(clickedRecipe.id)
            savingInProgress = false
            val updatedRecipe = clickedRecipe.copy(isFavourite = isFavourite)
            updateContent(updatedRecipe)
        }
    }

    private fun updateContent(clickedRecipe: RecipeUI) {
        recipesByTabUI
            ?.asSequence()
            ?.map { recipeByAreaUI ->
                updateRecipeByAreaUI(recipeByAreaUI, clickedRecipe)
            }
            ?.toList()
            ?.run {
                recipesByTabUI = this
                val content = MainScreenStates.Content(this)
                states.postValue(content)
            }
    }

    private fun updateRecipeByAreaUI(
        recipeByTabUI: RecipeByTabUI,
        clickedRecipe: RecipeUI
    ): RecipeByTabUI {
        var clickedRecipePosition = 0
        val recipes = recipeByTabUI.recipeByTab
            .asSequence()
            .mapIndexed { index, recipeUI ->
                if (recipeUI.id == clickedRecipe.id) {
                    clickedRecipePosition = index
                    clickedRecipe
                } else {
                    recipeUI
                }
            }
        return recipeByTabUI.copy(
            recipeByTab = recipes.toList(),
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
                    recipesByArea = result.contentListRecipes
                    val favourites = favouriteRepository.loadAll() ?: emptyList()
                    val recipes = result.contentListRecipes
                        .map {
                            RecipeByTabUI(
                                nameTab = it.nameArea,
                                recipeByTab = it.recipeByArea
                                    .map { recipe ->
                                        recipe.toUI(favourites)
                                    },
                                selectedRecipePosition = 0
                            )
                        }
                    recipesByTabUI = recipes
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }
        }
    }

    private fun Recipe.toUI(favourites: List<Recipe>) = RecipeUI(
        id = idMeal,
        recipeName = name,
        recipeImageUrl = image,
        isFavourite = favourites.find { favourite -> favourite.idMeal == idMeal } != null
    )
}

data class RecipeByTabUI(val nameTab: String, val recipeByTab: List<RecipeUI>, val selectedRecipePosition: Int)

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
    data class NavigateToDetailRandom(val recipe: RecipeDetail) : MainScreenAction()
    object NavigateToSearch : MainScreenAction()
}

sealed class MainScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()
    data class OnFavouriteClicked(val recipe: RecipeUI) : MainScreenEvent()
    object OnRandomClick : MainScreenEvent()

    object OnFavouriteListMenuClicked : MainScreenEvent()
    object OnFeedbackClicked : MainScreenEvent()
    object OnReady : MainScreenEvent()
    object OnClickedNation : MainScreenEvent()
    object OnClickedCategory : MainScreenEvent()
    data class OnRefreshClick(val selectedTab: String) : MainScreenEvent()
    object OnSearchClick : MainScreenEvent()
    object OnLoginDialogClick : MainScreenEvent()


}

sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object NoLogged : MainScreenStates()

    sealed class Error : MainScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipes: List<RecipeByTabUI>) : MainScreenStates()
}
