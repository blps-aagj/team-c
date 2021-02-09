package com.ivanmorgillo.corsoandroid.teamc.network

import com.ivanmorgillo.corsoandroid.teamc.Recipe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeAPI {
    // val per comunicare con il backend
    private val service: RecipeService

    init { // codice che viene eseguito quando creiamo un ogg RecipeAPI, costruttore
        // creo client
        val logging = HttpLoggingInterceptor() // fa vedere quello che ricevi
        logging.level = HttpLoggingInterceptor.Level.BODY // body per avere tutte le info
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create()) // collegato gson a retrofit
            .client(client)
            .build()
        service = retrofit.create(RecipeService::class.java)
    }

    suspend fun loadRecipes(): List<Recipe> {
        val recipeList = service.loadRecipes()
        return recipeList.meals.map {
            Recipe(
                name = it.strMeal,
                image = it.strMealThumb,
                idMeal = it.idMeal
            )
        }
    }
}
