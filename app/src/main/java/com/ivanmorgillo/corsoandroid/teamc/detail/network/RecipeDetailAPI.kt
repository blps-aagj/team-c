package com.ivanmorgillo.corsoandroid.teamc.detail.network

import com.ivanmorgillo.corsoandroid.teamc.Recipe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
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

    suspend fun loadDetailsRecipe(id: Int): LoadRecipesDetailResult {
        try {
            val recipeDetail = service.loadDetailsRecipe(id)
            TODO()
        } catch (e: IOException) {
            TODO()
        }
    }
}

// Gestisce il caso di un qualsiasi errore
sealed class LoadRecipesDetailError {
    object NoRecipeFound : LoadRecipesDetailError()
    object NoInternet : LoadRecipesDetailError()
    object SlowInternet : LoadRecipesDetailError()
    object ServerError : LoadRecipesDetailError()
}

// Gestisce i due casi possibili del load
sealed class LoadRecipesDetailResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipesDetailResult()
    data class Failure(val error: LoadRecipesDetailError) : LoadRecipesDetailResult()
}

interface RecipeDetailService {
    @POST("lookup.php?i=52772")
    suspend fun loadDetailsRecipe(id: Int): RecipeDetailDTO
}
