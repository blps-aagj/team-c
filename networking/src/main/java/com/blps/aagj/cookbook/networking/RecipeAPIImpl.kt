package com.ivanmorgillo.corsoandroid.teamc.network.home

import com.blps.aagj.cookbook.domain.AllRecipesByAreaResult
import com.blps.aagj.cookbook.domain.AllRecipesByAreaResult.AllRecipesByAreaError.GenericErrorByArea
import com.blps.aagj.cookbook.domain.AllRecipesByAreaResult.AllRecipesByAreaError.NoInternetByArea
import com.blps.aagj.cookbook.domain.AllRecipesByAreaResult.Failure
import com.blps.aagj.cookbook.networking.RecipeService
import com.blps.aagj.cookbook.networking.toDomain
import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeAPIImpl(private val service: RecipeService) : com.blps.aagj.cookbook.domain.RecipeAPI {

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
    override suspend fun loadAllRecipesByArea(): AllRecipesByAreaResult {
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
            AllRecipesByAreaResult.Success(recipesByArea)
        } catch (e: IOException) {
            Failure(NoInternetByArea)
        } catch (e: SocketTimeoutException) {
            Failure(NoInternetByArea)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadAreaResult")
            Failure(GenericErrorByArea)
        }
    }
}
