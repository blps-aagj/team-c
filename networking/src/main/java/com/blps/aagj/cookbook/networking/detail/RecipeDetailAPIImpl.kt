package com.blps.aagj.cookbook.networking.detail

import LoadRecipesDetailError
import LoadRecipesDetailResult
import com.blps.aagj.cookbook.domain.detail.RecipeDetailAPI
import com.blps.aagj.cookbook.networking.RecipeService
import org.json.JSONException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

class RecipeDetailAPIImpl(private val service: RecipeService) : RecipeDetailAPI {

    override suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult {
        return try {
            val recipeDetailList = service.loadDetailsRecipe(id.toString())
            //            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            Timber.d("recipeDetailList${recipeDetailList.meals}")
            if (recipeDetailList.meals != null) {
                val recipeDetail = recipeDetailList.meals.firstOrNull()
                if (recipeDetail == null) {
                    LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
                } else {
                    LoadRecipesDetailResult.Success(recipeDetail.toDomain())
                }
            } else {
                LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
            }
        } catch (e: IOException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoInternet)
        } catch (e: JSONException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoRecipeDetailFound)
        } catch (e: SocketTimeoutException) {
            LoadRecipesDetailResult.Failure(LoadRecipesDetailError.NoInternet)
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
