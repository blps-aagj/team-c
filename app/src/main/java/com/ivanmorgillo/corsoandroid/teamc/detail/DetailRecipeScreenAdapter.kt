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
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.ImageIngredientsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.TitleCategoryAreaViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.VideoInstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.exhaustive

sealed class DetailScreenItems {
    data class TitleCategoryArea(val title: String, val category: String, val area: String) :
        DetailScreenItems() // modellare il titolo della schermata

    data class ImageIngredients(val image: String, val ingredients: List<IngredientUI>) : DetailScreenItems()
    data class Instructions(val instructions: List<String>) : DetailScreenItems()
    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems()
}

private const val IMAGE_INGREDIENTS_VIEWTYPE = 2
private const val INSTRUCTIONS_VIEWTYPE = 3
private const val TITLE_CATEGORY_AREA_VIEWTYPE = 4
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
            is DetailScreenItems.TitleCategoryArea -> TITLE_CATEGORY_AREA_VIEWTYPE
            is DetailScreenItems.ImageIngredients -> IMAGE_INGREDIENTS_VIEWTYPE
            is DetailScreenItems.Instructions -> INSTRUCTIONS_VIEWTYPE
            is DetailScreenItems.VideoInstructions -> VIDEOINSTRUCTIONS_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailRecipeScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE_CATEGORY_AREA_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_title_category_area, parent, false)
                TitleCategoryAreaViewHolder(view)
            }
            IMAGE_INGREDIENTS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_image_ingredients, parent, false)
                ImageIngredientsViewHolder(view)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_instructions, parent, false)
                InstructionsViewHolder(view)
            }
            VIDEOINSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_video_instructions, parent, false)
                VideoInstructionsViewHolder(view)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailRecipeScreenViewHolder, position: Int) {
        when (holder) {
            is TitleCategoryAreaViewHolder -> holder.bind(items[position] as DetailScreenItems.TitleCategoryArea)
            is ImageIngredientsViewHolder -> holder.bind(items[position] as DetailScreenItems.ImageIngredients)
            is InstructionsViewHolder -> holder.bind(items[position] as DetailScreenItems.Instructions)
            is VideoInstructionsViewHolder -> Unit
        }.exhaustive
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

sealed class DetailRecipeScreenViewHolder(itemView: View) : ViewHolder(itemView) {
    // 1
    class TitleCategoryAreaViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeCategory: TextView = itemView.findViewById(R.id.detail_recipe_screen_category_value)
        private val detailRecipeArea: TextView = itemView.findViewById(R.id.detail_recipe_screen_area_value)
        private val detailRecipeTitle: TextView = itemView.findViewById(R.id.detail_screen_title)
        fun bind(titleCategoryArea: DetailScreenItems.TitleCategoryArea) {
            detailRecipeTitle.text = titleCategoryArea.title
            detailRecipeArea.text = titleCategoryArea.area
            detailRecipeCategory.text = titleCategoryArea.category
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
        private val detailRecipeInstructionsRecyclerView: RecyclerView = itemView.findViewById(R.id.recipe_detail_screen_instructions)
        fun bind(instructions: DetailScreenItems.Instructions) {
            val adapter = InstructionAdapter()
            detailRecipeInstructionsRecyclerView.adapter = adapter
            adapter.instructionsList = instructions.instructions
        }
    }

    class VideoInstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) // da IMPLEMENTARE
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
    val name: TextView = itemView.findViewById(R.id.detail_screen_ingredient_name)
    fun bind(item: IngredientUI) {
        name.text = item.name
    }
}

class InstructionAdapter : RecyclerView.Adapter<InstructionsItemViewHolder>() {
    var instructionsList: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_text_instruction, parent, false)
        return InstructionsItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionsItemViewHolder, position: Int) {
        holder.bind(instructionsList[position])
    }

    override fun getItemCount(): Int {
        return instructionsList.size
    }
}

class InstructionsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(instruction: String) {
        itemView.findViewById<TextView>(R.id.detail_single_instruction).text = instruction
    }
}

data class IngredientUI(val name: String, val measure: String)
