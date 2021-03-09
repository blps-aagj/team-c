import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FavouriteRepository {
    suspend fun loadAll(): List<Recipe>
    suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavourite(id: Long): Boolean
}

class FavouriteRepositoryImpl(
    private val context: Context,
    private val gson: Gson
) : FavouriteRepository {
    private val storage: SharedPreferences by lazy {
        context.getSharedPreferences("Favourites", Context.MODE_PRIVATE)
    }

    override suspend fun loadAll(): List<Recipe> = withContext(Dispatchers.IO) {
        storage.all
            .values
            .map {
                it as String
            }
            .map {
                gson.fromJson(it, RecipeEntity::class.java)
            }
            .map {
                Recipe(
                    name = it.name,
                    image = it.image,
                    idMeal = it.id
                )
            }
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun save(recipe: Recipe, isFavourite: Boolean) = withContext(Dispatchers.IO) {
        if (isFavourite) {
            val recipeEntity = RecipeEntity(name = recipe.name, image = recipe.image, id = recipe.idMeal)
            val serializedRecipe = gson.toJson(recipeEntity)
            storage.edit().putString(recipe.idMeal.toString(), serializedRecipe).commit()
        } else {
            storage.edit().remove(recipe.idMeal.toString()).commit()
        }
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        storage.edit().remove(id.toString()).commit()
    }

    override suspend fun isFavourite(id: Long): Boolean = withContext(Dispatchers.IO) {
        val maybeFavourite = storage.getString(id.toString(), null)
        maybeFavourite != null
    }
}

data class RecipeEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("id")
    val id: Long
)
