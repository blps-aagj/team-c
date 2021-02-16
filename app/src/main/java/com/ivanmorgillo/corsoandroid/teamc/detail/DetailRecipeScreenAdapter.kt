package com.ivanmorgillo.corsoandroid.teamc.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.R.layout.detail_recipe_screen_title
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.ImageIngredientsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.exhaustive

sealed class DetailScreenItems {
    data class Title(val title: String) : DetailScreenItems() // modelare il titolo della schermata
    data class CategoryArea(val category: String, val area: String) : DetailScreenItems()
    data class ImageIngredients(val image: String, val ingredients: List<IngredientUI>) : DetailScreenItems()
    data class Instructions(val instructions: List<String>) : DetailScreenItems()
    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems()
}

private const val CATEGORY_AREA_VIEWTYPE = 1
private const val IMAGE_INGREDIENTS_VIEWTYPE = 2
private const val INSTRUCTIONS_VIEWTYPE = 3
private const val TITLE_VIEWTYPE = 4
private const val VIDEOINSTRUCTIONS_VIEWTYPE = 5

class DetailRecipeScreenAdapter : RecyclerView.Adapter<DetailRecipeScreenViewHolder>() {
    var items = emptyList<DetailScreenItems>()
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
            is DetailScreenItems.Title -> TITLE_VIEWTYPE
            is DetailScreenItems.CategoryArea -> CATEGORY_AREA_VIEWTYPE
            is DetailScreenItems.ImageIngredients -> IMAGE_INGREDIENTS_VIEWTYPE
            is DetailScreenItems.Instructions -> INSTRUCTIONS_VIEWTYPE
            is DetailScreenItems.VideoInstructions -> VIDEOINSTRUCTIONS_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailRecipeScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_title, parent, false)
                DetailRecipeScreenViewHolder.TitleViewHolder(view)
            }
            CATEGORY_AREA_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_category_area, parent, false)
                DetailRecipeScreenViewHolder.CategoryAreaViewHolder(view)
            }
            IMAGE_INGREDIENTS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_image_ingredients, parent, false)
                DetailRecipeScreenViewHolder.ImageIngredientsViewHolder(view)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_instructions, parent, false)
                DetailRecipeScreenViewHolder.InstructionsViewHolder(view)
            }
            VIDEOINSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_video_instructions, parent, false)
                DetailRecipeScreenViewHolder.VideoInstructionsViewHolder(view)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailRecipeScreenViewHolder, position: Int) {
        when (holder) {
            is DetailRecipeScreenViewHolder.TitleViewHolder -> holder.bind(items[position] as DetailScreenItems.Title)
            is DetailRecipeScreenViewHolder.CategoryAreaViewHolder -> holder.bind(items[position] as DetailScreenItems.CategoryArea)
            is ImageIngredientsViewHolder -> holder.bind(items[position] as DetailScreenItems.ImageIngredients)
            is InstructionsViewHolder -> holder.bind(items[position] as DetailScreenItems.Instructions)
            is DetailRecipeScreenViewHolder.VideoInstructionsViewHolder -> {
            }
        }.exhaustive
    }

    override fun getItemCount(): Int {
        return items.size
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

        fun bind(categoryArea: DetailScreenItems.CategoryArea) {
            detailRecipeArea.text = categoryArea.area
            detailRecipeCategory.text = categoryArea.category
        }
    }

    class ImageIngredientsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeImage: ImageView = itemView.findViewById(R.id.detail_recipe_screen_image)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.detail_recipe_screen_ingredients)

        fun bind(imageIngredients: DetailScreenItems.ImageIngredients) {
            detailRecipeImage.load(imageIngredients.image)
            val adapter = IngredientsAdapter()
            recyclerView.adapter = adapter
            adapter.items = imageIngredients.ingredients
        }
    }

    class InstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeInstructions: TextView = itemView.findViewById(R.id.recipe_detail_screen_instructions)
        fun bind(instructions: DetailScreenItems.Instructions) {
            //  detailRecipeInstructions.text = instructions.instructions
        }
    }

    class VideoInstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {

    }
}

class IngredientsAdapter : RecyclerView.Adapter<IngredientsViewHolder>() {
    var items: List<IngredientUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_list_item, parent, false)
        return IngredientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class IngredientsViewHolder(itemView: View) : ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.detail_screen_ingredient_name)
    fun bind(item: IngredientUI) {
        name.text = item.name
    }
}

data class IngredientUI(val name: String, val measure: String)
