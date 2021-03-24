package com.blps.aagj.cookbook.networking.home

import Recipe
import RecipeByArea
import RecipeByCategory
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameError
import com.blps.aagj.cookbook.domain.home.LoadRecipeSearchByNameResult
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaError
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaResult
import com.blps.aagj.cookbook.domain.home.LoadRecipesByCategoryError
import com.blps.aagj.cookbook.domain.home.LoadRecipesByCategoryResult
import com.blps.aagj.cookbook.domain.home.LoadRecipesError
import com.blps.aagj.cookbook.domain.home.LoadRecipesResult
import com.blps.aagj.cookbook.domain.home.RecipeAPI
import com.blps.aagj.cookbook.networking.RecipeService
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeAPIImpl(private val service: RecipeService) : RecipeAPI {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadRecipesByArea(area: String): LoadRecipesResult {
        try {
            val recipeList = service.loadRecipesByArea(area)
            val recipes = recipeList.meals.mapNotNull {
                it.toDomain()
            }
            // caso lista vuota
            return if (recipes.isEmpty()) {
                LoadRecipesResult.Failure(LoadRecipesError.NoRecipeFound)
            } else {
                LoadRecipesResult.Success(recipes)
            }
        } catch (e: IOException) {
            return LoadRecipesResult.Failure(LoadRecipesError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadRecipesResult.Failure(LoadRecipesError.NoInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRecipes")
            return LoadRecipesResult.Failure(LoadRecipesError.GenericError)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadAllRecipesByArea(): LoadRecipesByAreaResult {
        return try {
            val areasList = service.loadAreas().areas
            val recipesByArea = areasList.map { areaDTO ->
                val recipes = service.loadRecipesByArea(areaDTO.strArea)
                    .meals.mapNotNull {
                        it.toDomain()
                    }
                RecipeByArea(
                    nameArea = areaDTO.strArea,
                    recipeByArea = recipes
                )
            }
            LoadRecipesByAreaResult.Success(recipesByArea)
        } catch (e: IOException) {
            LoadRecipesByAreaResult.Failure(LoadRecipesByAreaError.NoInternetByArea)
        } catch (e: SocketTimeoutException) {
            LoadRecipesByAreaResult.Failure(LoadRecipesByAreaError.NoInternetByArea)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadAreaResult")
            LoadRecipesByAreaResult.Failure(LoadRecipesByAreaError.GenericErrorByArea)
        }
    }

    override suspend fun loadRecipeSearchByName(name: String): LoadRecipeSearchByNameResult {
        return try {
            val recipeDTO = service.loadRecipeSearchByName(name)
            val recipes = recipeDTO.meals?.mapNotNull {
                val id = it.idMeal.toLongOrNull()
                if (id != null) {
                    Recipe(name = it.strMeal, image = it.strMealThumb, idMeal = id)
                } else {
                    null
                }
            }
            if (recipes != null) {
                LoadRecipeSearchByNameResult.Success(recipes)
            } else {
                LoadRecipeSearchByNameResult.Failure(LoadRecipeSearchByNameError.NoInternet)
            }
        } catch (e: IOException) {
            LoadRecipeSearchByNameResult.Failure(LoadRecipeSearchByNameError.NoInternet)
        } catch (e: SocketTimeoutException) {
            LoadRecipeSearchByNameResult.Failure(LoadRecipeSearchByNameError.NoInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRecipeSearchByName")
            LoadRecipeSearchByNameResult.Failure(LoadRecipeSearchByNameError.GenericError)
        }
    }

    override suspend fun loadRecipeByCategories(category: String): LoadRecipesResult {
        try {
            val recipeByCategoryDTO = service.loadRecipesByCategory(category)
            val recipesByCategory = recipeByCategoryDTO.meals.mapNotNull {
                it.toDomain()
            }
            return if (recipesByCategory.isEmpty()) {
                LoadRecipesResult.Failure(LoadRecipesError.NoRecipeFound)
            } else {
                LoadRecipesResult.Success(recipesByCategory)
            }
        } catch (e: IOException) {
            return LoadRecipesResult.Failure(LoadRecipesError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadRecipesResult.Failure(LoadRecipesError.NoInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRecipes")
            return LoadRecipesResult.Failure(LoadRecipesError.GenericError)
        }
    }

    override suspend fun loadAllRecipesByCategory(): LoadRecipesByCategoryResult {
        return try {
            val categoryList = service.loadCategories().categories
            val recipeByCategory = categoryList.map { categoryDTO ->
                val recipes = service.loadRecipesByCategory(categoryDTO.strCategory)
                    .meals.mapNotNull {
                        it.toDomain()
                    }
                RecipeByCategory(
                    nameCategory = categoryDTO.strCategory,
                    recipeByCategory = recipes
                )
            }
            LoadRecipesByCategoryResult.Success(recipeByCategory)
        } catch (e: IOException) {
            LoadRecipesByCategoryResult.Failure(LoadRecipesByCategoryError.NoInternetByCategory)
        } catch (e: SocketTimeoutException) {
            LoadRecipesByCategoryResult.Failure(LoadRecipesByCategoryError.NoInternetByCategory)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadAreaResult")
            LoadRecipesByCategoryResult.Failure(LoadRecipesByCategoryError.GenericErrorByArea)
        }
    }
}
