package com.ivanmorgillo.corsoandroid.teamc.detail.network

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.R.layout.detail_recipe_screen_category_area
import com.ivanmorgillo.corsoandroid.teamc.detail.network.DetailRecipeScreenViewHolder.ImageIngredientsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.network.DetailRecipeScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.network.DetailScreenItems.CategoryArea
import com.ivanmorgillo.corsoandroid.teamc.exhaustive

//data class RecipeIngredient(val ingredientName: String, val ingredientQuantity: String)

sealed class DetailScreenItems {
    data class Title(val title: String) : DetailScreenItems() // modelare il titolo della schermata
    data class CategoryArea(val category: String, val area: String) : DetailScreenItems()
    data class ImageIngredients(val image: String, val ingredients: List<String>) : DetailScreenItems()

    //     data class ImageIngredients(val image: String, val ingredients: List<String>) : DetailScreenItems()
    // data class ImageIngredients(val image: String, val ingredients: String) : DetailScreenItems()
    data class Instructions(val instructions: String) : DetailScreenItems()

    /* data class Instructions(val instructions: List<String>) : DetailScreenItems() */
    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems()
}

private const val CATEGORY_AREA_VIEWTYPE = 1
private const val IMAGE_INGREDIENTS_VIEWTYPE = 2
private const val INSTRUCTIONS_VIEWTYPE = 3
private const val TITLE_VIEWTYPE = 4
private const val VIDEOINSTRUCTIONS_VIEWTYPE = 5

class DetailRecipeScreenAdapter : RecyclerView.Adapter<DetailRecipeScreenViewHolder>() {
    private var items = emptyList<DetailScreenItems>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Get item view type
     *
     * @param position
     * @return un intero che rappresenta al view type
     */
    override fun getItemViewType(position: Int): Int {
        val item = this.items[position]
        return when (item) {
            is CategoryArea -> CATEGORY_AREA_VIEWTYPE
            is DetailScreenItems.ImageIngredients -> IMAGE_INGREDIENTS_VIEWTYPE
            is DetailScreenItems.Instructions -> INSTRUCTIONS_VIEWTYPE
            is DetailScreenItems.Title -> TITLE_VIEWTYPE
            is DetailScreenItems.VideoInstructions -> VIDEOINSTRUCTIONS_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailRecipeScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CATEGORY_AREA_VIEWTYPE -> {
                val view = layoutInflater.inflate(detail_recipe_screen_category_area, parent, false)
                InstructionsViewHolder(view)
            }
            IMAGE_INGREDIENTS_VIEWTYPE -> {
                TODO()
            }
            INSTRUCTIONS_VIEWTYPE -> {
                TODO()
            }
            TITLE_VIEWTYPE -> {
                TODO()
            }
            VIDEOINSTRUCTIONS_VIEWTYPE -> {
                TODO()
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailRecipeScreenViewHolder, position: Int) {
        when (holder) {
            is DetailRecipeScreenViewHolder.CategoryAreaViewHolder -> holder.bind(items[position] as CategoryArea)
            is ImageIngredientsViewHolder -> holder.bind(items[position] as DetailScreenItems.ImageIngredients)
            is DetailRecipeScreenViewHolder.IngredientsListViewHolder -> TODO()
            is InstructionsViewHolder -> holder.bind(items[position] as DetailScreenItems.Instructions)
            is DetailRecipeScreenViewHolder.TitleViewHolder -> holder.bind(items[position] as DetailScreenItems.Title)
        }.exhaustive
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}

sealed class DetailRecipeScreenViewHolder(itemView: View) : ViewHolder(itemView) {
    // 1
    class TitleViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeTitle: TextView = itemView.findViewById<TextView>(R.id.detail_recipe_screen_title)
        fun bind(title: DetailScreenItems.Title) {
            detailRecipeTitle.text = title.title
        }
    }

    // 3
    class CategoryAreaViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeCategory: TextView = itemView.findViewById(R.id.detail_recipe_screen_category)
        private val detailRecipeArea: TextView = itemView.findViewById(R.id.detail_recipe_screen_area)

        fun bind(categoryArea: CategoryArea) {
            detailRecipeArea.text = categoryArea.area
            detailRecipeCategory.text = categoryArea.category
        }
    }

    class ImageIngredientsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeImage: ImageView = itemView.findViewById(R.id.detail_recipe_screen_image)
        private val detailRecipeIngredients: TextView = itemView.findViewById(R.id.detail_recipe_screen_ingredients)

        fun bind(imageIngredients: DetailScreenItems.ImageIngredients) {
            detailRecipeImage.load(imageIngredients.image)
            //detailRecipeIngredients.text = imageIngredients.ingredients
        }
    }

    class InstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeInstructions: TextView = itemView.findViewById(R.id.recipe_detail_screen_instructions)
        fun bind(instructions: DetailScreenItems.Instructions) {
            detailRecipeInstructions.text = instructions.instructions
        }
    }

    /* da completare*/
    class IngredientsListViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recipe_detail_screen_ingredient_list)
        fun bind(ingredientList: String) {
            /* adapter = IngredientsListAdapter()
            adapter.setIngredientList()*/
        }
    }
}
