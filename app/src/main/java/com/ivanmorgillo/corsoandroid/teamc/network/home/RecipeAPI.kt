package com.ivanmorgillo.corsoandroid.teamc.network.home

import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe
import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.AllRecipesByAreaError.GenericErrorByArea
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.AllRecipesByAreaError.NoInternetByArea
import com.ivanmorgillo.corsoandroid.teamc.home.AllRecipesByAreaResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.GenericError
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoInternet
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoRecipeFound
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeAPI {
    private val service: RecipeService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(RecipeService::class.java)
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRecipes(area: String): LoadRecipesResult {
        try {
            val recipeList = service.loadRecipes(area)
            val recipes = recipeList.meals.mapNotNull {
                it.toDomain()
            }
            // caso lista vuota
            return if (recipes.isEmpty()) {
                LoadRecipesResult.Failure(NoRecipeFound)
            } else {
                LoadRecipesResult.Success(recipes)
            }
        } catch (e: IOException) {
            return LoadRecipesResult.Failure(NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadRecipesResult.Failure(NoInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRecipes")
            return LoadRecipesResult.Failure(GenericError)
        }
    }

    private fun RecipeDTO.Meal.toDomain(): Recipe? {
        val id = idMeal.toLongOrNull()
        return if (id != null) {
            Recipe(
                name = strMeal,
                image = strMealThumb,
                idMeal = id
            )
        } else {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadAllRecipesByArea(): AllRecipesByAreaResult {
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

sealed class LoadRecipesError {
    object NoRecipeFound : LoadRecipesError()
    object NoInternet : LoadRecipesError()
    object GenericError : LoadRecipesError()
}

sealed class LoadRecipesResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipesResult()
    data class Failure(val error: LoadRecipesError) : LoadRecipesResult()
}
