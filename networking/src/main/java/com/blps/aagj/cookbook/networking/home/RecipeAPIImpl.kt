package com.blps.aagj.cookbook.networking.home

import RecipeByArea
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaError
import com.blps.aagj.cookbook.domain.home.LoadRecipesByAreaResult
import com.blps.aagj.cookbook.domain.home.LoadRecipesError
import com.blps.aagj.cookbook.domain.home.LoadRecipesResult
import com.blps.aagj.cookbook.domain.home.RecipeAPI
import com.blps.aagj.cookbook.networking.RecipeService
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeAPIImpl(private val service: RecipeService) : RecipeAPI {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        try {
            val recipeList = service.loadRecipes(area)
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
                val recipes = service.loadRecipes(areaDTO.strArea)
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
}
