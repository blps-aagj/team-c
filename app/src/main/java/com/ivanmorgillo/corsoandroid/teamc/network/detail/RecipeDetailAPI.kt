package com.ivanmorgillo.corsoandroid.teamc.network.detail

import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeDetail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
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
        return try {
            val recipeDetailList = service.loadDetailsRecipe(id.toString())
            //            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            Timber.d("recipeDetailList${recipeDetailList.meals}")
            if (recipeDetailList.meals != null) {
                val recipeDetail = recipeDetailList.meals.first()
                LoadRecipesDetailResult.Success(recipeDetail.toDomain())
            } else {
                LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
            }
        } catch (e: IOException) {
            TODO()
        } catch (e: JSONException) {
            TODO()
        }
    }

    suspend fun loadDetailsRecipeRandom(): LoadRecipesDetailResult {
        try {
            val recipeDetailList = service.loadDetailsRecipeRandom()
//            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            val recipeDetail = recipeDetailList.meals.firstOrNull()
            return if (recipeDetail == null) {
                LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
            } else {
                LoadRecipesDetailResult.Success(recipeDetail.toDomain())
            }
        } catch (e: IOException) {
            TODO()
        } catch (e: JSONException) {
            TODO()
        }
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
    data class Success(val recipesDetail: RecipeDetail) : LoadRecipesDetailResult()
    data class Failure(val error: LoadRecipesDetailError) : LoadRecipesDetailResult()
}
