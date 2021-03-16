import com.google.firebase.auth.ktx.auth
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
    private val favouritesCollection by lazy {
        val uid = Firebase.auth.currentUser.uid
        firestore.collection("favourites-$uid")
    }

    override suspend fun loadAll(): List<Recipe>? {
        val favouriteList = favouritesCollection
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
        return if (favouriteList.isEmpty()) {
            null
        } else {
            favouriteList
        }
    }

    override suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            "id" to recipe.idMeal,
            "name" to recipe.name,
            "image" to recipe.image
        )
        favouritesCollection
            .document(recipe.idMeal.toString())
            .set(favouriteMap)
            .await()
        return true
    }

    override suspend fun delete(id: Long): Boolean {
        favouritesCollection
            .document(id.toString())
            .delete()
            .await()
        return true
    }

    override suspend fun isFavourite(id: Long): Boolean {
        val tmp = favouritesCollection
            .document(id.toString())
            .get()
            .await()
        return tmp.exists()
    }
}
