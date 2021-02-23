package com.ivanmorgillo.corsoandroid.teamc.network

import com.ivanmorgillo.corsoandroid.teamc.Recipe
import com.ivanmorgillo.corsoandroid.teamc.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.network.LoadAreaResult.Failure
import com.ivanmorgillo.corsoandroid.teamc.network.LoadAreaResult.Success
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.NoInternet
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.ServerError
import com.ivanmorgillo.corsoandroid.teamc.network.LoadRecipesError.SlowInternet
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
//            Timber.e("recipeList $recipeList")
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
    suspend fun loadArea(): LoadAreaResult {
        /*
        * try-catch devo gestire errore di chiamata di rete, si può trovare in due stati, funzionante o rotto
        * uso le sealed class
        */
        try {
            val areaList = service.loadArea()
            val areas = areaList.meals.map {
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

    private suspend fun AreaDTO.Meal.toDomain(): RecipeByArea {
        var recipeByArea = emptyList<Recipe>()
        when (val result = loadRecipes(strArea)) {
            is LoadRecipesResult.Failure -> Timber.e("AreaDTO Meal toDomain() ")
            is LoadRecipesResult.Success -> {
                recipeByArea = result.recipes
            }
        }.exhaustive
        return RecipeByArea(
            nameArea = strArea,
            recipeByArea = recipeByArea
        )
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
    data class Success(val recipes: List<RecipeByArea>) : LoadAreaResult()
    data class Failure(val error: LoadRecipesError) : LoadAreaResult()
}
