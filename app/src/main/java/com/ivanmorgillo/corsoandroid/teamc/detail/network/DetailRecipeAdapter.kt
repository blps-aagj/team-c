package com.ivanmorgillo.corsoandroid.teamc.detail.network

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R

data class RecipeIngredient(val ingredientName: String, val ingredientQuantity: String)

sealed class DetailScreenItems {
    data class Title(val title: String) : DetailScreenItems() // modelare il titolo della schermata
    data class CategoryArea(val category: String, val area: String) : DetailScreenItems()

    //     data class ImageIngredients(val image: String, val ingredients: List<RecipeIngredient>) : DetailScreenItems()
//     data class ImageIngredients(val image: String, val ingredients: List<String>) : DetailScreenItems()
    data class ImageIngredients(val image: String, val ingredients: String) : DetailScreenItems()
    data class Instructions(val instructions: String) : DetailScreenItems()

    /* data class Instructions(val instructions: List<String>) : DetailScreenItems() */
    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems()
}

class DetailScreenAdapter : RecyclerView.Adapter<DetailScreenViewHolder>() {
    var items: List<DetailScreenItems> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}

sealed class DetailScreenViewHolder(itemView: View) : ViewHolder(itemView) {
    // 1
    class TitleViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val detailRecipeTitle: TextView = itemView.findViewById<TextView>(R.id.detail_recipe_screen_title)
        fun bind(title: DetailScreenItems.Title) {
            detailRecipeTitle.text = title.title
        }
    }

    // 3
    class CategoryAreaViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val detailRecipeCategory: TextView = itemView.findViewById(R.id.detail_recipe_screen_category)
        private val detailRecipeArea: TextView = itemView.findViewById(R.id.detail_recipe_screen_area)

        fun bind(categoryArea: DetailScreenItems.CategoryArea) {
            detailRecipeArea.text = categoryArea.area
            detailRecipeCategory.text = categoryArea.category
        }
    }

    class ImageIngredientsViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val detailRecipeImage: ImageView = itemView.findViewById(R.id.detail_recipe_screen_image)
        private val detailRecipeIngredients: TextView = itemView.findViewById(R.id.detail_recipe_screen_ingredients)

        fun bind(imageIngredients: DetailScreenItems.ImageIngredients) {
            detailRecipeImage.load(imageIngredients.image)
            detailRecipeIngredients.text = imageIngredients.ingredients
        }
    }

    class InstructionsViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val detailRecipeInstructions: TextView = itemView.findViewById(R.id.recipe_detail_screen_instructions)
        fun bind(instructions: DetailScreenItems.Instructions) {
            detailRecipeInstructions.text = instructions.instructions
        }
    }

    /* da completare*/
    class IngredientsListViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recipe_detail_screen_ingredient_list)
        fun bind(ingredientList: String) {
            //val adapter = IngredientsListAdapter()
            //adapter.setIngredientList()
        }
    }
}
/*da completare*/
//class IngredientsListAdapter : RecyclerView.Adapter<>() {
//
//    fun setIngredientList(sad){
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun onBindViewHolder(holder: String, position: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getItemCount(): Int {
//        TODO("Not yet implemented")
//    }
//
//}

