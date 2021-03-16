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
    private val favouritesCollection = firestore.collection("favourites")

    override suspend fun loadAll(): List<Recipe>? {
        return favouritesCollection
            .get()
            .await()
            .documents
            .map {
                val name = it["name"] as String
                val image = it["image"] as String
                val id = it["id"] as Long
                Recipe(
                    name = name,
                    image = image,
                    idMeal = id
                )
            }
    }

    override suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            "id" to recipe.idMeal,
            "name" to recipe.name,
            "image" to recipe.image
        )
        favouritesCollection.add(favouriteMap).await()
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
