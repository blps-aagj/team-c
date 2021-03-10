data class RecipeDetail(
    val recipeId: String,
    val recipeName: String,
    val recipeCategory: String,
    val recipeArea: String,
    val recipeInstructions: List<String>,
    val recipeImage: String,
    val recipeIngredientsAndMeasures: List<Ingredient>,
    val recipeVideoInstructions: String
)

data class Ingredient(val ingredientName: String, val ingredientQuantity: String)
