package com.blps.aagj.cookbook.networking

import com.blps.aagj.cookbook.domain.LoadRecipesDetailError
import com.blps.aagj.cookbook.domain.LoadRecipesDetailResult
import com.blps.aagj.cookbook.domain.RecipeDetailAPI
import org.json.JSONException
import timber.log.Timber
import java.io.IOException

class RecipeDetailAPIImpl(private val service: RecipeService) : RecipeDetailAPI {

    override suspend fun loadDetailsRecipe(id: Long): LoadRecipesDetailResult {
        return try {
            val recipeDetailList = service.loadDetailsRecipe(id.toString())
            //            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
            Timber.d("recipeDetailList${recipeDetailList.meals}")
            if (recipeDetailList.meals != null) {
                val recipeDetail = recipeDetailList.meals?.firstOrNull()
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
