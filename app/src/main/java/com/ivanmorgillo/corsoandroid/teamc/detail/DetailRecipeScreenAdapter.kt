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
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.TitleCategoryAreaViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.VideoInstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.Image
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.Ingredients
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.Instructions
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.TitleCategoryArea
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailScreenItems.VideoInstructions
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.ImageLoaderForCaching
import com.ivanmorgillo.corsoandroid.teamc.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

sealed class DetailScreenItems {
    data class TitleCategoryArea(val title: String, val category: String, val area: String) :
        DetailScreenItems() // modellare il titolo della schermata

    data class Image(val image: String) : DetailScreenItems()
    data class Ingredients(val ingredients: List<IngredientUI>) : DetailScreenItems()
    data class Instructions(val instructions: List<String>) : DetailScreenItems()
    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems()
}

private const val IMAGE_VIEWTYPE = 1
private const val INGREDIENTS_VIEWTYPE = 2
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
            is TitleCategoryArea -> TITLE_CATEGORY_AREA_VIEWTYPE
            is Image -> IMAGE_VIEWTYPE
            is Instructions -> INSTRUCTIONS_VIEWTYPE
            is VideoInstructions -> VIDEOINSTRUCTIONS_VIEWTYPE
            is Ingredients -> INGREDIENTS_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailRecipeScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE_CATEGORY_AREA_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_title_category_area, parent, false)
                TitleCategoryAreaViewHolder(view)
            }
            IMAGE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_image, parent, false)
                ImageViewHolder(view)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_instructions, parent, false)
                InstructionsViewHolder(view)
            }
            VIDEOINSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_video_instructions, parent, false)
                VideoInstructionsViewHolder(view)
            }
            INGREDIENTS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_recipe_screen_ingredients, parent, false)
                DetailRecipeScreenViewHolder.IngredientsViewHolder(view)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailRecipeScreenViewHolder, position: Int) {
        when (holder) {
            is TitleCategoryAreaViewHolder -> holder.bind(items[position] as TitleCategoryArea)
            is ImageViewHolder -> holder.bind(items[position] as Image)
            is InstructionsViewHolder -> holder.bind(items[position] as Instructions)
            is VideoInstructionsViewHolder -> holder.bind(items[position] as VideoInstructions)
            is DetailRecipeScreenViewHolder.IngredientsViewHolder -> holder.bind(items[position] as Ingredients)
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
        fun bind(titleCategoryArea: TitleCategoryArea) {
            detailRecipeTitle.text = titleCategoryArea.title
            detailRecipeArea.text = titleCategoryArea.area
            detailRecipeCategory.text = titleCategoryArea.category
        }
    }

    class ImageViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeImage: ImageView = itemView.findViewById(R.id.detail_recipe_screen_image)

        fun bind(image: Image) {
            detailRecipeImage.load(image.image, ImageLoaderForCaching.imageLoader(itemView.context))
        }
    }

    class IngredientsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.detail_recipe_screen_ingredients)

        fun bind(ingredients: Ingredients) {
            val adapter = IngredientsAdapter()
            recyclerView.adapter = adapter
            adapter.items = ingredients.ingredients
        }
    }

    class InstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val detailRecipeInstructionsRecyclerView: RecyclerView = itemView.findViewById(R.id.recipe_detail_screen_instructions)
        fun bind(instructions: Instructions) {
            val adapter = InstructionAdapter()
            detailRecipeInstructionsRecyclerView.adapter = adapter
            adapter.instructionsList = instructions.instructions
        }
    }

    class VideoInstructionsViewHolder(itemView: View) : DetailRecipeScreenViewHolder(itemView) {
        private val youTubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtube_player_view)
        fun bind(item: VideoInstructions) {
            if (item.videoInstructions.isEmpty()) {
                youTubePlayerView.gone()
            } else {
                youTubePlayerView.visible()
                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(item.videoInstructions, 0f)
                        youTubePlayer.pause()
                    }
                })
            }
        }
    }
}

class IngredientsAdapter : RecyclerView.Adapter<IngredientsItemViewHolder>() {
    var items: List<IngredientUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_recipe_screen_ingredient_item, parent, false)
        return IngredientsItemViewHolder(view)
    }

    override fun onBindViewHolder(holderItem: IngredientsItemViewHolder, position: Int) {
        holderItem.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class IngredientsItemViewHolder(itemView: View) : ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.detail_screen_ingredient_name)
    private val quantity: TextView = itemView.findViewById(R.id.detail_screen_ingredient_quantity)
    private val ingredientImage = itemView.findViewById<ImageView>(R.id.ingredient_image)
    fun bind(item: IngredientUI) {
        ingredientImage.load("https://www.themealdb.com/images/ingredients/${item.name}-Small.png") {
            crossfade(true)
        }
        name.text = item.name
        quantity.text = item.measure
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
