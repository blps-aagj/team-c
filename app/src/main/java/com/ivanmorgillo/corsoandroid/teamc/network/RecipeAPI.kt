package com.ivanmorgillo.corsoandroid.teamc.network

import com.ivanmorgillo.corsoandroid.teamc.Recipe
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.NoInternet
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.ServerError
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesResult.Success
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException

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

    @Suppress("TooGenericExceptionCaught") // per non far vedere da detekt err eccezione troppo generica
    suspend fun loadRecipes(): LoadRecipesResult {
        /*  
        * try-catch devo gestire errore di chiamata di rete, si può trovare in due stati, funzionante o rotto
        * uso le sealed class
        */
        try {
            val recipeList = service.loadRecipes()
            val recipes = recipeList.meals.map {
                Recipe(
                    name = it.strMeal,
                    image = it.strMealThumb,
                    idMeal = it.idMeal
                )
            }
            // caso lista vuota
            return if (recipes.isEmpty()) {
                Failure(NoRecipeFound)
            } else {
                Success(recipes)
            }
        } catch (e: IOException) {
            return Failure(NoInternet)
        } catch (e: SocketTimeoutException) {
            return Failure(SlowInternet)
        } catch (e: Exception) {
            return Failure(ServerError)
        }
    }
}

// Gestisce il caso di un qualsiasi errore
sealed class LoadRecipesError {
    object NoRecipeFound : LoadRecipesError()
    object NoInternet : LoadRecipesError()
    object SlowInternet : LoadRecipesError()
    object ServerError : LoadRecipesError()
}

// Gestisce i due casi possibili del load
sealed class LoadRecipesResult {
    data class Success(val recipes: List<Recipe>) : LoadRecipesResult()
    data class Failure(val error: LoadRecipesError) : LoadRecipesResult()
}
