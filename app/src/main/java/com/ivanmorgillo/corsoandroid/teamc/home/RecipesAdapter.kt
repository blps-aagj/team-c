package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.databinding.RecipeItemBinding

class RecipesAdapter(private val onclick: (RecipeUI) -> Unit, private val onFavouriteClicked: (RecipeUI) -> Unit) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    private var recipes: List<RecipeUI> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false /*è sempre false questo parametro*/
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], onclick, onFavouriteClicked)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(listOfRecipes: List<RecipeUI>) {
        recipes = listOfRecipes
        // dobbiamo notificare che abbiamo aggiunto delle ricette
        notifyDataSetChanged()
    }
}

class RecipeViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RecipeUI, onclick: (RecipeUI) -> Unit, onFavouriteClicked: (RecipeUI) -> Unit) {
        binding.recipeTitle.text = item.recipeName
        binding.recipeImage.load(item.recipeImageUrl) {
            crossfade(true)
        }
        binding.recipeImage.contentDescription = item.recipeName
        binding.recipeRoot.setOnClickListener { onclick(item) }
        binding.favouriteListCheckboxLayout.icon.setOnClickListener {
            onFavouriteClicked(item)
        }
        binding.favouriteListCheckboxLayout.icon.isChecked = item.isFavourite
    }
}

data class RecipeUI(
    val id: Long,
    val recipeName: String,
    val recipeImageUrl: String,
    val isFavourite: Boolean
)
