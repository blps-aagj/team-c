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
import com.ivanmorgillo.corsoandroid.teamc.home.HomeScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val repository: RecipesRepository,
    private val favouriteRepository: FavouriteRepository,
    private val detailsRepository: RecipesDetailsRepository,
    private val tracking: Tracking,
    private val authenticationManager: AuthenticationManager
) : ViewModel() {

    val states = MutableLiveData<HomeScreenStates>()
    val actions = SingleLiveEvent<HomeScreenAction>()
    private var recipesByArea: List<RecipeByArea>? = null
    private var recipesByCategory: List<RecipeByCategory>? = null
    private var recipesByTabUI: List<RecipeByTabUI>? = null

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: HomeScreenEvent) {
        when (event) {
            HomeScreenEvent.OnReady -> loadContent(false)
            is HomeScreenEvent.OnRecipeClick -> {
                tracking.logEvent("home_recipe_clicked")
                actions.postValue(NavigateToDetail(event.recipe))
            }
            is HomeScreenEvent.OnRefreshClick -> {
                tracking.logEvent("home_refresh_clicked")
                onRefreshClick(event)
            }
            is HomeScreenEvent.OnFavouriteClicked -> {
                tracking.logEvent("home_favorite_clicked")
                onFavouriteClicked(event)
            }
            is HomeScreenEvent.OnRandomClick -> {
                tracking.logEvent("home_random_clicked")
                loadDetailRandomRecipe()
            }
            HomeScreenEvent.OnSearchClick -> {
                tracking.logEvent("home_search_clicked")
                Timber.d("OnSearchClick")
                actions.postValue(HomeScreenAction.NavigateToSearch)
            }
            HomeScreenEvent.OnLoginDialogClick -> {
                tracking.logEvent("login_dialog_clicked_home")
            }
            HomeScreenEvent.OnClickedCategory -> {
                tracking.logEvent("clicked_tab_category")
                loadCategoryContent(false)
            }
            HomeScreenEvent.OnClickedNation -> {
                tracking.logEvent("clicked_tab_nation")
                loadContent(false)
            }
        }.exhaustive
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun onFavouriteClicked(event: HomeScreenEvent.OnFavouriteClicked) =
        if (authenticationManager.isUserLoggedIn()) {
            viewModelScope.launch {
                if (!savingInProgress) {
                    saveFavourite(event.recipe)
                } else {
                    Timber.d("saving is in progress")
                }
            }
        } else {
            states.postValue(HomeScreenStates.NoLogged)
        }

    private fun onRefreshClick(event: HomeScreenEvent.OnRefreshClick) {
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
        states.postValue(HomeScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAllRecipesByCategory(forced)
            when (result) {
                is LoadRecipesByCategoryResult.Failure -> {
                    states.postValue(HomeScreenStates.Error.NoNetwork)
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
                    states.postValue(HomeScreenStates.Content(recipes))
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
                val content = HomeScreenStates.Content(this)
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
        states.postValue(HomeScreenStates.Loading)
        return viewModelScope.launch {
            when (val result = detailsRepository.loadDetailsRecipesRandom()) {
                is LoadRecipesDetailResult.Failure -> {
                    Timber.d("RecipeId failure")
                    states.postValue(HomeScreenStates.Error.NoNetwork)
                }
                is LoadRecipesDetailResult.Success -> {
                    Timber.d("RecipeId passed")
                    val recipeDetail = result.recipesDetail
                    actions.postValue(HomeScreenAction.NavigateToDetailRandom(recipeDetail))
                }
            }.exhaustive
        }
    }

    private fun loadContent(forced: Boolean) {
        states.postValue(HomeScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAllRecipesByArea(forced)
            when (result) {
                is LoadRecipesByAreaResult.Failure -> states.postValue(HomeScreenStates.Error.NoNetwork)
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
                    states.postValue(HomeScreenStates.Content(recipes))
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

sealed class HomeScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : HomeScreenAction()
    data class NavigateToDetailRandom(val recipe: RecipeDetail) : HomeScreenAction()
    object NavigateToSearch : HomeScreenAction()
}

sealed class HomeScreenEvent {
    data class OnRecipeClick(val recipe: RecipeUI) : HomeScreenEvent()
    data class OnFavouriteClicked(val recipe: RecipeUI) : HomeScreenEvent()
    object OnRandomClick : HomeScreenEvent()
    object OnReady : HomeScreenEvent()
    object OnClickedNation : HomeScreenEvent()
    object OnClickedCategory : HomeScreenEvent()
    data class OnRefreshClick(val selectedTab: String) : HomeScreenEvent()
    object OnSearchClick : HomeScreenEvent()
    object OnLoginDialogClick : HomeScreenEvent()
}

sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    object NoLogged : HomeScreenStates()

    sealed class Error : HomeScreenStates() {
        object NoNetwork : Error()
        object NoRecipeFound : Error()
    }

    data class Content(val recipes: List<RecipeByTabUI>) : HomeScreenStates()
}
