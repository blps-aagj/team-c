// lista di ricette: lista di oggetti con nome, immagine e id
data class Recipe(val name: String, val image: String, val idMeal: Long)
data class RecipeByArea(val nameArea: String, val recipeByArea: List<Recipe>)
data class RecipeByCategory(val nameCategory: String, val recipeByCategory: List<Recipe>)
