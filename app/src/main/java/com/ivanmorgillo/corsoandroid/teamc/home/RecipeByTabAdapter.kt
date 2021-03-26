package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.databinding.AreaItemBinding
import com.ivanmorgillo.corsoandroid.teamc.utils.getFlag
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader

class RecipeByTabAdapter(private val onclick: (RecipeUI) -> Unit, private val onFavouriteClicked: (RecipeUI) -> Unit) :
    RecyclerView.Adapter<RecipeByAreaViewHolder>() {

    var recipeByTab: List<RecipeByTabUI> = emptyList()
        set(value) {
            val diffCallBack = RecipeByAreaUIDiffCallBack(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallBack)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeByAreaViewHolder {
        val binding = AreaItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false // è sempre false questo parametro
        )
        return RecipeByAreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeByAreaViewHolder, position: Int) {
        holder.bind(recipeByTab[position], onclick, onFavouriteClicked)
    }

    override fun getItemCount(): Int {
        return recipeByTab.size
    }
}

class RecipeByAreaViewHolder(private val binding: AreaItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeByTabUI, onclick: (RecipeUI) -> Unit, onFavouriteClicked: (RecipeUI) -> Unit) {
        binding.recipeAreaTitle.text = item.nameTab

        val adapter = RecipesAdapter(onclick, onFavouriteClicked)
        binding.recipeAreaRecyclerview.adapter = adapter
        adapter.recipes = item.recipeByTab
        binding.recipeAreaRecyclerview.scrollToPosition(item.selectedRecipePosition)
        val areaFlag = getFlag(item.nameTab)
        val flagUri = "https://www.countryflags.io/$areaFlag/shiny/64.png"
        binding.recipeAreaFlagIcon.load(flagUri, imageLoader(itemView.context))
    }
}

class RecipeByAreaUIDiffCallBack(private val oldList: List<RecipeByTabUI>, private val newList: List<RecipeByTabUI>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.nameTab == newItem.nameTab
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.nameTab == newItem.nameTab && oldItem.recipeByTab == newItem.recipeByTab
    }
}
