package com.ivanmorgillo.corsoandroid.teamc.network.detail

import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeDetail
import com.ivanmorgillo.corsoandroid.teamc.network.RecipeService
import org.json.JSONException
import timber.log.Timber
import java.io.IOException

interface RecipeDetailAPI {
    suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult

    suspend fun loadDetailsRecipeRandom(): LoadRecipesDetailResult
}

class RecipeDetailAPIImpl(private val service: RecipeService) : RecipeDetailAPI {

    override suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult {
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
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoInternet)
        } catch (e: JSONException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
        }
    }

    override suspend fun loadDetailsRecipeRandom(): LoadRecipesDetailResult {
        return try {
            val recipeDetailList = service.loadDetailsRecipeRandom()
//            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            val recipeDetail = recipeDetailList.meals?.firstOrNull()
            return if (recipeDetail == null) {
                LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
            } else {
                LoadRecipesDetailResult.Success(recipeDetail.toDomain())
            }
        } catch (e: IOException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoInternet)
        } catch (e: JSONException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
        }
    }
}

// Gestisce il caso di un qualsiasi errore
sealed class LoadRecipesDetailError {
    object NoRecipeDetailFound : LoadRecipesDetailError()
    object NoInternet : LoadRecipesDetailError()
}

// Gestisce i due casi possibili del load
sealed class LoadRecipesDetailResult {
    data class Success(val recipesDetail: RecipeDetail) : LoadRecipesDetailResult()
    data class Failure(val error: LoadRecipesDetailError) : LoadRecipesDetailResult()
}
