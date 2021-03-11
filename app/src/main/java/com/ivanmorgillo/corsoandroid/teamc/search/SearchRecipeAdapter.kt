package com.ivanmorgillo.corsoandroid.teamc.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.Coil.imageLoader
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.databinding.SearchListItemBinding
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI

class SearchRecipeAdapter : RecyclerView.Adapter<SearchViewHolder>() {
    var items: List<RecipeUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

class SearchViewHolder(val binding: SearchListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeUI) {
        binding.imageSearchRecipe.load(item.recipeImageUrl, imageLoader(itemView.context))
        binding.titleSearchRecipe.text = item.recipeName
    }
}
