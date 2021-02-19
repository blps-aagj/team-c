package com.ivanmorgillo.corsoandroid.teamc.detail.network

import com.ivanmorgillo.corsoandroid.teamc.detail.Ingredient
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetail
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
        try {
            val recipeDetailList = service.loadDetailsRecipe(id.toString())
            Timber.d("recipeDetailList $recipeDetailList") // id non funziona ancora
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

    private fun RecipeDetailDTO.Meal.toDomain(): RecipeDetail {

        // da migliorare la soluzione
        val ingredientList: List<Ingredient> = listOfNotNull(
            validateIngredientsAndMeasures(strIngredient1, strMeasure1),
            validateIngredientsAndMeasures(strIngredient2, strMeasure2),
            validateIngredientsAndMeasures(strIngredient3, strMeasure3),
            validateIngredientsAndMeasures(strIngredient4, strMeasure4),
            validateIngredientsAndMeasures(strIngredient5, strMeasure5),
            validateIngredientsAndMeasures(strIngredient6, strMeasure6),
            validateIngredientsAndMeasures(strIngredient7, strMeasure7),
            validateIngredientsAndMeasures(strIngredient8, strMeasure8),
            validateIngredientsAndMeasures(strIngredient9, strMeasure9),
            validateIngredientsAndMeasures(strIngredient10, strMeasure10),
            validateIngredientsAndMeasures(strIngredient11, strMeasure11),
            validateIngredientsAndMeasures(strIngredient12, strMeasure12),
            validateIngredientsAndMeasures(strIngredient13, strMeasure13),
            validateIngredientsAndMeasures(strIngredient14, strMeasure14),
            validateIngredientsAndMeasures(strIngredient15, strMeasure15),
            validateIngredientsAndMeasures(strIngredient16, strMeasure16),
            validateIngredientsAndMeasures(strIngredient17, strMeasure17),
            validateIngredientsAndMeasures(strIngredient18, strMeasure18),
            validateIngredientsAndMeasures(strIngredient19, strMeasure19),
            validateIngredientsAndMeasures(strIngredient20, strMeasure20),
        )

        return RecipeDetail(
            recipeName = strMeal,
            recipeCategory = strCategory,
            recipeArea = strArea,
            recipeInstructions = loadRecipeInstruction(strInstructions),
            recipeImage = strMealThumb,
            recipeIngredientsAndMeasures = ingredientList,
            recipeVideoInstructions = strYoutube
        )
    }

    private fun loadRecipeInstruction(instructions: String): List<String> {
        return instructions.split("/r/n")
    }

    private fun validateIngredientsAndMeasures(ingredientName: String?, ingredientQuantity: String?): Ingredient? {
        // inserire check per vedere se la stringa è fatta solo di numeri
        return if (ingredientName.isNullOrBlank()) {
            null
        } else {
            if (ingredientQuantity.isNullOrBlank()) {
                Ingredient(ingredientName, "q.s.")
            } else {
                Ingredient(ingredientName, ingredientQuantity)
            }
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
