package com.ivanmorgillo.corsoandroid.teamc.detail.network

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
        val ingredientList: List<String?> = listOf(
            strIngredient1,
            strIngredient2,
            strIngredient3,
            strIngredient4,
            strIngredient5,
            strIngredient6,
            strIngredient7,
            strIngredient8,
            strIngredient9,
            strIngredient10,
            strIngredient11,
            strIngredient12,
            strIngredient13,
            strIngredient14,
            strIngredient15,
            strIngredient16,
            strIngredient17,
            strIngredient18,
            strIngredient19,
            strIngredient20
        )
        val measureList: List<String?> = listOf(
            strMeasure1,
            strMeasure2,
            strMeasure3,
            strMeasure4,
            strMeasure5,
            strMeasure6,
            strMeasure7,
            strMeasure8,
            strMeasure9,
            strMeasure10,
            strMeasure11,
            strMeasure12,
            strMeasure13,
            strMeasure14,
            strMeasure15,
            strMeasure16,
            strMeasure17,
            strMeasure18,
            strMeasure19,
            strMeasure20
        )
        return RecipeDetail(
            recipeName = strMeal,
            recipeCategory = strCategory,
            recipeArea = strArea,
            recipeInstructions = loadRecipeInstruction(strInstructions),
            recipeImage = strMealThumb,
            recipeIngredientsAndMeasures = listOf(), // da implementare
            recipeVideoInstructions = strYoutube
        )
    }

    private fun loadRecipeInstruction(instructions: String): List<String> {
        return instructions.split("/r/n")
    }

//    private fun loadRecipeIngredients(ingredientsList: List<String?>, measureList: List<String?>): List<IngredientUI> {
//        val ingredientListWithoutNull = ingredientsList.filterNotNull()
//        val measureListWithoutNull = measureList.filterNotNull()
//
//        // Creare oggetto di tipo Ingredients, se il nome é null non deve essere inserito nella lista ingredienti, buttare anche la misura corrispondente.
//    }

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
