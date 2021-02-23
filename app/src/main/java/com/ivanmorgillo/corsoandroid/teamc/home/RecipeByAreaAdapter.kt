package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.RecipeByAreaUI

class RecipeByAreaAdapter(private val onclick: (RecipeUI) -> Unit) : RecyclerView.Adapter<RecipeByAreaViewHolder>() {
    private var recipeByArea: List<RecipeByAreaUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeByAreaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.area_item,
            parent,
            false /*è sempre false questo parametro*/
        )
        return RecipeByAreaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeByAreaViewHolder, position: Int) {
        holder.bind(recipeByArea[position], onclick)
    }

    override fun getItemCount(): Int {
        return recipeByArea.size
    }

    fun setRecipesByArea(recipeByArea: List<RecipeByAreaUI>) {
        this.recipeByArea = recipeByArea
        notifyDataSetChanged()
    }
}

class RecipeByAreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val recipeAreaTitle = itemView.findViewById<TextView>(R.id.recipe_area_title)
    private val recipeAreaRecyclerView = itemView.findViewById<RecyclerView>(R.id.recipe_area_recyclerview)
    fun bind(item: RecipeByAreaUI, onclick: (RecipeUI) -> Unit) {
        recipeAreaTitle.text = item.nameArea
//        Timber.e("RecipeByAreaViewHolder ${item.recipeByArea}")
        val adapter = RecipesAdapter(onclick)
        recipeAreaRecyclerView.adapter = adapter
        adapter.setRecipes(item.recipeByArea)
    }
}
