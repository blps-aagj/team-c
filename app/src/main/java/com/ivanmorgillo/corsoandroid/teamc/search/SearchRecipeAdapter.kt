package com.ivanmorgillo.corsoandroid.teamc.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.Coil.imageLoader
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.databinding.SearchListItemBinding
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI

class SearchRecipeAdapter : RecyclerView.Adapter<FavouriteViewHolder>() {
    var items: List<RecipeUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

class FavouriteViewHolder(val binding: SearchListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeUI) {
        binding.imageSearchRecipe.load(item.recipeImageUrl, imageLoader(itemView.context))
        binding.titleSearchRecipe.text = item.recipeName
    }
}

