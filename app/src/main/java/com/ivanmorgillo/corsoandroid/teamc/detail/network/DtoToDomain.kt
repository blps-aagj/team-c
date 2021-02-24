package com.ivanmorgillo.corsoandroid.teamc.detail.network

import com.ivanmorgillo.corsoandroid.teamc.detail.Ingredient
import com.ivanmorgillo.corsoandroid.teamc.detail.RecipeDetail

fun RecipeDetailDTO.Meal.toDomain(): RecipeDetail {

    // da migliorare la soluzione
    val ingredientList: List<Ingredient> = getIngredients()

    return RecipeDetail(
        recipeName = strMeal,
        recipeCategory = strCategory,
        recipeArea = strArea,
        recipeInstructions = loadRecipeInstruction(strInstructions),
        recipeImage = strMealThumb,
        recipeIngredientsAndMeasures = ingredientList,
        recipeVideoInstructions = getVideoID(strYoutube)
    )
}

fun getVideoID(videoUri: String?): String {
    return if (videoUri != null && videoUri.isNotBlank()) {
        videoUri.replace("https://www.youtube.com/watch?v=", "")
    } else {
        ""
    }
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
