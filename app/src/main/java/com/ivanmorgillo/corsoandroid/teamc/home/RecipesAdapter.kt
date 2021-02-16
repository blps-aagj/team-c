package com.ivanmorgillo.corsoandroid.teamc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView

// dobbiamo creare l'adapter alla recyclerview. Adapter, quello della recyclerview, vuole un viewholder come tipo
// L'adapter riceve una lista di oggetti che viene processata nell'onCreate e nell'onBinde per creare i viewHolder
// il viewholder è uno degli elementi visibile nella lista.
class RecipesAdapter(private val onclick: (RecipeUI) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    // questa variabile contiene gli elementi da passare all'adapter
    private var recipes: List<RecipeUI> = emptyList()

    // viene chiamato per ogni elemento della lista per creare la sua rappresentazione in kotlin
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        // a RecipeViewHolder dobbiamo passare una view. Un oggetto view è una rappresentazione android di un xml.

        // andiamo a creare una view. Il LayoutInflater ci serve per fare l'inflate
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recipe_item,
            parent,
            false /*è sempre false questo parametro*/
        )
        return RecipeViewHolder(view)
    }

    // questo metodo collega il viewholder con un elemento della lista (collega la UI con un item)
    // mette in comunicazione i dati che vengono dall'esterno con la parte grafica
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], onclick)
    }

    // ci dice qunti elementi ci sono nella lista
    override fun getItemCount(): Int {
        return recipes.size
    }

    /**
     * Set recipes prende in input una lista di ricette
     *
     * @param listOfRecipes
     */
    fun setRecipes(listOfRecipes: List<RecipeUI>) {
        recipes = listOfRecipes
        // dobbiamo notificare che abbiamo aggiunto delle ricette
        notifyDataSetChanged()
    }
}

/**
 * Recipe view holder
 *
 * @constructor
 *
 * @param itemView rappresenta la nostra card
 */
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val recipeTitle = itemView.findViewById<TextView>(R.id.recipe_title)
    private val recipeImage = itemView.findViewById<ImageView>(R.id.recipe_image)
    private val recipeCardView = itemView.findViewById<MaterialCardView>(R.id.recipe_root)

    // creiamo un metodo bind
    fun bind(item: RecipeUI, onclick: (RecipeUI) -> Unit) {
        recipeTitle.text = item.recipeName // mette in comunicazione la textview con le info nella lista
        recipeImage.load(item.recipeImageUrl)
        recipeImage.contentDescription = item.recipeName
        recipeCardView.setOnClickListener { onclick(item) }
    }
}

// mappa l' oggetto che sta nella card
/**
 * Recipe u i
 *
 * @property recipeName
 * @property recipeImageUrl
 * @constructor Create empty Recipe u i
 */
data class RecipeUI(
    val id: Long,
    val recipeName: String,
    val recipeImageUrl: String
)
