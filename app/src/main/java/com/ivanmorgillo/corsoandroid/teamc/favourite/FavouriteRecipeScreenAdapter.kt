package com.ivanmorgillo.corsoandroid.teamc.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.databinding.FavouriteListItemBinding
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader

class FavouriteRecipeScreenAdapter(private val onClick: (FavouriteRecipeUI) -> Unit) : RecyclerView.Adapter<FavouriteViewHolder>() {
    var items: List<FavouriteRecipeUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = FavouriteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) = holder.bind(items[position], onClick)

    override fun getItemCount(): Int = items.size
}

class FavouriteViewHolder(val binding: FavouriteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FavouriteRecipeUI, onClick: (FavouriteRecipeUI) -> Unit) {
        binding.imageFavouriteRecipe.load(item.imageRecipe, imageLoader(itemView.context))
        binding.titleFavouriteRecipe.text = item.titleRecipe
        binding.root.setOnClickListener { onClick(item) }
    }
}

data class FavouriteRecipeUI(
    val idRecipe: Long,
    val imageRecipe: String,
    val titleRecipe: String
)
