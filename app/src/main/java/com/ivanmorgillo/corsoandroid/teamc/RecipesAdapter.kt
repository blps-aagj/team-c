package com.ivanmorgillo.corsoandroid.teamc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

// dobbiamo creare l'adapter alla reclyclerview. Adapter, quello della recyclerview, vuole un viewholder come tipo
// L'adapter riceve una lista di oggetti che viene processata nell'onCreate e nell'onBinde per creare i viewHolder
// il viewholder è uno degli elementi visibile nella lista.
class RecipesAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
    // questa variabile contiene gli elementi da passare all'adapter
    private var recipies: List<RecipeUI> = emptyList()

    // viene chiamato per ogni elemento della lista per creare la sua rappresentazione in kotlin
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        // a RecipeViewHolder dobbiamo passare una view. Unoggetto view è una rappresentazione android di un xml.

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
        holder.bind(recipies[position])
    }

    // ci dice qunti elementi ci sono nella lista
    override fun getItemCount(): Int {
        return recipies.size
    }

    fun setRecipies(listOfRecipies: List<RecipeUI>){
        recipies = listOfRecipies
        // dobbiamo notificare che abbiamo aggiunto delle ricette
        notifyDataSetChanged()
    }
}

// itemView rappresenta la nostra card
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val recipeTitle = itemView.findViewById<TextView>(R.id.recipe_title)
    private val recipeImage = itemView.findViewById<ImageView>(R.id.recipe_image)
    // creiamo un metodo bind
    fun bind(item: RecipeUI) {
        recipeTitle.text = item.recipeName // mette in comunicazione la textview con le info nella lista
        recipeImage.load(item.recipeImageUrl)
    }
}