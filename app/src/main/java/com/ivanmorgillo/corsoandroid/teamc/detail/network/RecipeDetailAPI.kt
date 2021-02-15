package com.ivanmorgillo.corsoandroid.teamc.detail.network

import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

class RecipeDetailAPI {

    private val service: RecipeDetailService

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
        service = retrofit.create(RecipeDetailService::class.java)
    }

    suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult {
        try {
            val recipeDetailList = service.loadDetailsRecipe(id.toString())
            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            val recipeDetail = recipeDetailList.meals.map {
                it.toDomain()
            }
            return if (recipeDetail.isEmpty()) {
                LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
            } else {
                LoadRecipesDetailResult.Success(recipeDetail)
            }
        } catch (e: IOException) {
            TODO()
        }
    }

    private fun RecipeDetailDTO.Meal.toDomain(): RecipeDetail {
        return RecipeDetail(
            recipeName = strMeal,
            recipeCategory = strCategory,
            recipeArea = strArea,
            recipeInstructions = listOf(), // da implementare
            recipeImage = strMealThumb,
            recipeIngredientsAndMeasures = listOf(), // da implementare
            recipeVideoInstructions = strYoutube
        )
    }
}

// Gestisce il caso di un qualsiasi errore
sealed class LoadRecipesDetailError {
    object NoRecipeDetailFound : LoadRecipesDetailError()
    object NoInternet : LoadRecipesDetailError()
    object SlowInternet : LoadRecipesDetailError()
    object ServerError : LoadRecipesDetailError()
}
// Gestisce i due casi possibili del load
sealed class LoadRecipesDetailResult {
    data class Success(val recipesDetail: List<RecipeDetail>) : LoadRecipesDetailResult()
    data class Failure(val error: LoadRecipesDetailError) : LoadRecipesDetailResult()
}
