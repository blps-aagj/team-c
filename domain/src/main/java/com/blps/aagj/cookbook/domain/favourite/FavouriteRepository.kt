import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FavouriteRepository {
    suspend fun loadAll(): List<Recipe>?
    suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavourite(id: Long): Boolean
}

class FavouriteRepositoryImpl : FavouriteRepository {
    private val db by lazy {
        Firebase.database.reference
    }
    private val favourites = db.child("favourites")

    override suspend fun loadAll(): List<Recipe>? = suspendCoroutine { continuation ->
        favourites.get().addOnSuccessListener {
            val entity = it.children
                .mapNotNull {
                    it.getValue(RecipeEntity::class.java)
                }.mapNotNull {
                    if (it.name != null && it.image != null && it.id != null) {
                        Recipe(name = it.name, image = it.image, idMeal = it.id)
                    } else {
                        null
                    }
                }
            continuation.resume(entity)
        }
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun save(recipe: Recipe, isFavourite: Boolean): Boolean = suspendCoroutine { continuation ->
        if (isFavourite) {
            val recipeEntity = RecipeEntity(name = recipe.name, image = recipe.image, id = recipe.idMeal)
            favourites.child(recipe.idMeal.toString())
                .setValue(recipeEntity)
                .addOnFailureListener {
                    Timber.e("Success save $it")
                    continuation.resume(false)
                }
                .addOnSuccessListener {
                    Timber.d("Successfully save recipe ${recipe.idMeal}")
                    continuation.resume(true)
                }
        } else {
            favourites.child(recipe.idMeal.toString())
                .removeValue()
                .addOnFailureListener {
                    Timber.e("Failure remove $it")
                    continuation.resume(false)
                }
                .addOnSuccessListener {
                    Timber.d("Successfully remove recipe ${recipe.idMeal}")
                    continuation.resume(false)
                }
        }
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        favourites.child(id.toString()).removeValue()
        true
    }

    override suspend fun isFavourite(id: Long): Boolean = suspendCoroutine {
        favourites.child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entity = snapshot.getValue(RecipeEntity::class.java)
                val isFavourite = entity != null
                it.resume(isFavourite)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }
}

data class RecipeEntity(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("id")
    val id: Long? = null
)
