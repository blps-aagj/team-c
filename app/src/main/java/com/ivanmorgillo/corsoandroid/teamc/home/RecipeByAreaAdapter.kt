package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.RecipeByAreaUI
import com.ivanmorgillo.corsoandroid.teamc.databinding.AreaItemBinding
import com.ivanmorgillo.corsoandroid.teamc.utils.getFlag
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader

class RecipeByAreaAdapter(private val onclick: (RecipeUI) -> Unit, private val onFavouriteClicked: (RecipeUI) -> Unit) :
    RecyclerView.Adapter<RecipeByAreaViewHolder>() {
    private var recipeByArea: List<RecipeByAreaUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeByAreaViewHolder {
        val binding = AreaItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false // è sempre false questo parametro
        )
        return RecipeByAreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeByAreaViewHolder, position: Int) {
        holder.bind(recipeByArea[position], onclick, onFavouriteClicked)
    }

    override fun getItemCount(): Int {
        return recipeByArea.size
    }

    fun setRecipesByArea(recipeByArea: List<RecipeByAreaUI>) {
        this.recipeByArea = recipeByArea
        notifyDataSetChanged()
    }
}

class RecipeByAreaViewHolder(private val binding: AreaItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeByAreaUI, onclick: (RecipeUI) -> Unit, onFavouriteClicked: (RecipeUI) -> Unit) {
        binding.recipeAreaTitle.text = item.nameArea
        val adapter = RecipesAdapter(onclick, onFavouriteClicked)
        binding.recipeAreaRecyclerview.adapter = adapter
        adapter.setRecipes(item.recipeByArea)
        val areaFlag = getFlag(item.nameArea)
        val flagUri = "https://www.countryflags.io/$areaFlag/shiny/64.png"
        binding.recipeAreaFlagIcon.load(flagUri, imageLoader(itemView.context))
    }
}
