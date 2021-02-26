package com.ivanmorgillo.corsoandroid.teamc.network.home

import com.ivanmorgillo.corsoandroid.teamc.domain.Area
import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult.Success
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoInternet
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.ServerError
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesError.SlowInternet
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
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
    suspend fun loadRecipes(area: String): LoadRecipesResult {
        /*
        * try-catch devo gestire errore di chiamata di rete, si può trovare in due stati, funzionante o rotto
        * uso le sealed class
        */
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
            return LoadRecipesResult.Failure(SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRecipes")
            return LoadRecipesResult.Failure(ServerError)
        }
    }

    @Suppress("TooGenericExceptionCaught") // per non far vedere da detekt err eccezione troppo generica
    suspend fun loadAreas(): LoadAreaResult {
        /*
        * try-catch devo gestire errore di chiamata di rete, si può trovare in due stati, funzionante o rotto
        * uso le sealed class
        */
        try {
            val areaList = service.loadArea()
            val areas = areaList.meals.mapNotNull {
                it.toDomain()
            }
            // caso lista vuota
            return if (areas.isEmpty()) {
                Failure(NoRecipeFound)
            } else {
                Success(areas)
            }
        } catch (e: IOException) {
            return Failure(NoInternet)
        } catch (e: SocketTimeoutException) {
            return Failure(SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadAreaResult")
            return Failure(ServerError)
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

    private fun AreaDTO.Meal.toDomain(): Area? {
        val area = strArea
        return if (area.isBlank()) {
            null
        } else {
            Area(
                nameArea = area,
            )
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

sealed class LoadAreaResult {
    data class Success(val areas: List<Area>) : LoadAreaResult()
    data class Failure(val error: LoadRecipesError) : LoadAreaResult()
}
