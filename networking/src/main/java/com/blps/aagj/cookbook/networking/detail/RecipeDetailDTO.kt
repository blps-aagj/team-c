package com.blps.aagj.cookbook.networking.detail

import androidx.annotation.Keep
import com.blps.aagj.cookbook.domain.detail.Ingredient
import com.blps.aagj.cookbook.domain.detail.RecipeDetail
import com.google.gson.annotations.SerializedName

@Keep
data class RecipeDetailDTO(
    @SerializedName("meals")
    val meals: List<Meal>?
) {
    @Keep
    data class Meal(
        @SerializedName("dateModified")
        val dateModified: String?,
        @SerializedName("idMeal")
        val idMeal: String,
        @SerializedName("strArea")
        val strArea: String,
        @SerializedName("strCategory")
        val strCategory: String,
        @SerializedName("strDrinkAlternate")
        val strDrinkAlternate: String?,
        @SerializedName("strIngredient1")
        val strIngredient1: String?,
        @SerializedName("strIngredient10")
        val strIngredient10: String?,
        @SerializedName("strIngredient11")
        val strIngredient11: String?,
        @SerializedName("strIngredient12")
        val strIngredient12: String?,
        @SerializedName("strIngredient13")
        val strIngredient13: String?,
        @SerializedName("strIngredient14")
        val strIngredient14: String?,
        @SerializedName("strIngredient15")
        val strIngredient15: String?,
        @SerializedName("strIngredient16")
        val strIngredient16: String?,
        @SerializedName("strIngredient17")
        val strIngredient17: String?,
        @SerializedName("strIngredient18")
        val strIngredient18: String?,
        @SerializedName("strIngredient19")
        val strIngredient19: String?,
        @SerializedName("strIngredient2")
        val strIngredient2: String?,
        @SerializedName("strIngredient20")
        val strIngredient20: String?,
        @SerializedName("strIngredient3")
        val strIngredient3: String?,
        @SerializedName("strIngredient4")
        val strIngredient4: String?,
        @SerializedName("strIngredient5")
        val strIngredient5: String?,
        @SerializedName("strIngredient6")
        val strIngredient6: String?,
        @SerializedName("strIngredient7")
        val strIngredient7: String?,
        @SerializedName("strIngredient8")
        val strIngredient8: String?,
        @SerializedName("strIngredient9")
        val strIngredient9: String?,
        @SerializedName("strInstructions")
        val strInstructions: String,
        @SerializedName("strMeal")
        val strMeal: String,
        @SerializedName("strMealThumb")
        val strMealThumb: String,
        @SerializedName("strMeasure1")
        val strMeasure1: String?,
        @SerializedName("strMeasure10")
        val strMeasure10: String?,
        @SerializedName("strMeasure11")
        val strMeasure11: String?,
        @SerializedName("strMeasure12")
        val strMeasure12: String?,
        @SerializedName("strMeasure13")
        val strMeasure13: String?,
        @SerializedName("strMeasure14")
        val strMeasure14: String?,
        @SerializedName("strMeasure15")
        val strMeasure15: String?,
        @SerializedName("strMeasure16")
        val strMeasure16: String?,
        @SerializedName("strMeasure17")
        val strMeasure17: String?,
        @SerializedName("strMeasure18")
        val strMeasure18: String?,
        @SerializedName("strMeasure19")
        val strMeasure19: String?,
        @SerializedName("strMeasure2")
        val strMeasure2: String?,
        @SerializedName("strMeasure20")
        val strMeasure20: String?,
        @SerializedName("strMeasure3")
        val strMeasure3: String?,
        @SerializedName("strMeasure4")
        val strMeasure4: String?,
        @SerializedName("strMeasure5")
        val strMeasure5: String?,
        @SerializedName("strMeasure6")
        val strMeasure6: String?,
        @SerializedName("strMeasure7")
        val strMeasure7: String?,
        @SerializedName("strMeasure8")
        val strMeasure8: String?,
        @SerializedName("strMeasure9")
        val strMeasure9: String?,
        @SerializedName("strSource")
        val strSource: String?,
        @SerializedName("strTags")
        val strTags: String?,
        @SerializedName("strYoutube")
        val strYoutube: String?
    )
}

fun RecipeDetailDTO.Meal.toDomain(): RecipeDetail {

    // da migliorare la soluzione
    val ingredientList: List<Ingredient> = getIngredients()

    return RecipeDetail(
        recipeId = idMeal,
        recipeName = strMeal,
        recipeCategory = strCategory,
        recipeArea = strArea,
        recipeInstructions = loadRecipeInstruction(strInstructions),
        recipeImage = strMealThumb,
        recipeIngredientsAndMeasures = ingredientList,
        recipeVideoInstructions = getVideoID(strYoutube),
    )
}

private fun RecipeDetailDTO.Meal.getIngredients() = listOfNotNull(
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

fun getVideoID(videoUri: String?): String {
    return if (videoUri != null && videoUri.isNotBlank()) {
        videoUri.replace("https://www.youtube.com/watch?v=", "")
    } else {
        ""
    }
}

fun loadRecipeInstruction(instructions: String): List<String> {
    return instructions.split("/r/n")
}

fun validateIngredientsAndMeasures(ingredientName: String?, ingredientQuantity: String?): Ingredient? {
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
