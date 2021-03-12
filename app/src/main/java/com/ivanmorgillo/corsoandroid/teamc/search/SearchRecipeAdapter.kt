package com.ivanmorgillo.corsoandroid.teamc.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.Coil.imageLoader
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.SearchCardviewBinding
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI
import timber.log.Timber

class SearchRecipeAdapter(private val onclick: (RecipeUI) -> Unit) : RecyclerView.Adapter<SearchViewHolder>() {
    var items: List<RecipeUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) = holder.bind(items[position], onclick)

    override fun getItemCount(): Int = items.size
}

class SearchViewHolder(val binding: SearchCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeUI, onclick: (RecipeUI) -> Unit) {
        binding.titleSearchRecipe.text = item.recipeName
        binding.imageSearchRecipe.load(item.recipeImageUrl, imageLoader(itemView.context)) {
            target(
                onError = {
                    Timber.e("Image not load ${item.recipeImageUrl}")
                },
                onSuccess = { result ->
                    binding.progressBarRecipeItem.gone()
                    binding.imageSearchRecipe.setImageDrawable(result)
                })
            error(R.drawable.ic_broken_image)

        }
        binding.recipeRoot.setOnClickListener { onclick(item) }
    }
}
