import com.blps.aagj.cookbook.domain.AuthenticationManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FavouriteRepository {
    suspend fun loadAll(): List<Recipe>?
    suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavourite(id: Long): Boolean
}

private const val RECIPE_NAME_KEY = "name"
private const val RECIPE_IMAGE_KEY = "image"
private const val RECIPE_USER_ID_KEY = "userID"
private const val RECIPE_ID_KEY = "recipeID"

class FavouriteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authenticationManager: AuthenticationManager
) : FavouriteRepository {

    private val favouritesCollection by lazy {
        firestore.collection("favourites")
    }

    override suspend fun loadAll(): List<Recipe>? {
        val uid = authenticationManager.getUid() ?: return null
        val favouriteList = favouritesCollection
            .whereEqualTo(RECIPE_USER_ID_KEY, uid)
            .get()
            .await()
            .documents
            .map {
                documentToRecipe(it)
            }
        return if (favouriteList.isEmpty()) {
            null
        } else {
            favouriteList
        }
    }

    private fun documentToRecipe(it: DocumentSnapshot): Recipe {
        val name = it[RECIPE_NAME_KEY] as String
        val image = it[RECIPE_IMAGE_KEY] as String
        val id = it[RECIPE_ID_KEY] as Long
        return Recipe(
            name = name,
            image = image,
            idMeal = id
        )
    }

    override suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            RECIPE_ID_KEY to recipe.idMeal,
            RECIPE_NAME_KEY to recipe.name,
            RECIPE_IMAGE_KEY to recipe.image,
            RECIPE_USER_ID_KEY to authenticationManager.getUid()
        )
        favouritesCollection
            .document()
            .set(favouriteMap)
            .await()
        return true
    }

    //
    override suspend fun delete(id: Long): Boolean {
        val uid = authenticationManager.getUid() ?: return false
        val matchingDocuments = favouritesCollection
            .whereEqualTo(RECIPE_USER_ID_KEY, uid)
            .whereEqualTo(RECIPE_ID_KEY, id)
            .get()
            .await().documents
        return if (matchingDocuments.isEmpty()) {
            false
        } else {
            val documentToDelete = matchingDocuments.first()
            favouritesCollection.document(documentToDelete.id).delete().await()
            true
        }
    }

    override suspend fun isFavourite(id: Long): Boolean {
        val uid = authenticationManager.getUid() ?: return false
        val tmp = favouritesCollection
            .whereEqualTo(RECIPE_USER_ID_KEY, uid)
            .whereEqualTo(RECIPE_ID_KEY, id)
            .get()
            .await()
        return !tmp.isEmpty
    }
}
