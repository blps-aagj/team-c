import com.blps.aagj.cookbook.domain.detail.RecipeDetail
import com.blps.aagj.cookbook.domain.detail.RecipeDetailAPI

interface RecipesDetailsRepository {
    suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult
    suspend fun loadDetailsRecipesRandom(): LoadRecipesDetailResult
}

class RecipesDetailRepositoryImpl(private val recipeDetailAPI: RecipeDetailAPI) : RecipesDetailsRepository {
    override suspend fun loadDetailsRecipes(id: Long): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipe(id)
    }

    override suspend fun loadDetailsRecipesRandom(): LoadRecipesDetailResult {
        return recipeDetailAPI.loadDetailsRecipeRandom()
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
