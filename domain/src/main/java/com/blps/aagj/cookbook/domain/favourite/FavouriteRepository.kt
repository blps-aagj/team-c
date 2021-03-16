import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface FavouriteRepository {
    suspend fun loadAll(): List<Recipe>?
    suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavourite(id: Long): Boolean
}

class FavouriteRepositoryImpl : FavouriteRepository {

    private val firestore by lazy {
        Firebase.firestore
    }

    override suspend fun loadAll(): List<Recipe>? {
        return emptyList()
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            "id" to recipe.idMeal,
            "name" to recipe.name,
            "image" to recipe.image
        )
        firestore.collection("favourites").add(favouriteMap).await()
        return true
    }

    override suspend fun delete(id: Long): Boolean {
        return true
    }

    override suspend fun isFavourite(id: Long): Boolean {
        return false
    }
}

data class RecipeEntity(
    val name: String? = null,
    val image: String? = null,
    val id: Long? = null
)
