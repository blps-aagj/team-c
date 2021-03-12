package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.RecipeItemBinding
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader
import timber.log.Timber

class RecipesAdapter(private val onclick: (RecipeUI) -> Unit, private val onFavouriteClicked: (RecipeUI) -> Unit) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    var recipes: List<RecipeUI> = emptyList()
        set(value) {
            val diffCallBack = RecipeUIDiffCallBack(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallBack)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

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
}

class RecipeViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RecipeUI, onclick: (RecipeUI) -> Unit, onFavouriteClicked: (RecipeUI) -> Unit) {
        binding.recipeTitle.text = item.recipeName
        binding.recipeImage.load(item.recipeImageUrl, imageLoader(binding.root.context)) {
            target(
                onError = {
                    Timber.e("Image not load ${item.recipeImageUrl}")
                },
                onSuccess = { result ->
                    binding.progressBarRecipeItem.gone()
                    binding.recipeImage.setImageDrawable(result)
                }
            )
            error(R.drawable.ic_broken_image)
        }
        binding.recipeImage.contentDescription = item.recipeName
        if (item.isFavourite) {
            binding.favouriteListDetailLayout.icon.setImageResource(R.drawable.ic_favourite_list)
        } else {
            binding.favouriteListDetailLayout.icon.setImageResource(R.drawable.ic_favourite_border_list)
        }
        binding.recipeRoot.setOnClickListener { onclick(item) }
        binding.favouriteListDetailLayout.icon.setOnClickListener {
            onFavouriteClicked(item)
        }
    }
}

class RecipeUIDiffCallBack(private val oldList: List<RecipeUI>, private val newList: List<RecipeUI>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id && oldItem.isFavourite == newItem.isFavourite
    }
}

data class RecipeUI(
    val id: Long,
    val recipeName: String,
    val recipeImageUrl: String,
    val isFavourite: Boolean
)
