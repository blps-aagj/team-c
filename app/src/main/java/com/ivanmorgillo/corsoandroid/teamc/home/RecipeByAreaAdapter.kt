package com.ivanmorgillo.corsoandroid.teamc.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.RecipeByAreaUI
import com.ivanmorgillo.corsoandroid.teamc.utils.COUNTRYABBREVIATOR

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
    private val recipeImageIcon = itemView.findViewById<ImageView>(R.id.recipe_area_title_icon)
    fun bind(item: RecipeByAreaUI, onclick: (RecipeUI) -> Unit) {
        recipeAreaTitle.text = item.nameArea
//        Timber.e("RecipeByAreaViewHolder ${item.recipeByArea}")
        val adapter = RecipesAdapter(onclick)
        recipeAreaRecyclerView.adapter = adapter
        adapter.setRecipes(item.recipeByArea)
        val prova = COUNTRYABBREVIATOR[item.nameArea] ?: ""
        recipeImageIcon.load("https://www.countryflags.io/$prova/shiny/64.png")
    }
}
