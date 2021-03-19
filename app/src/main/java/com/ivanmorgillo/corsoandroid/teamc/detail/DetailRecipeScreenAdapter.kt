package com.ivanmorgillo.corsoandroid.teamc.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailRecipeScreenImageBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailRecipeScreenIngredientItemBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailRecipeScreenIngredientsBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailRecipeScreenInstructionsBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailRecipeScreenVideoInstructionsBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailScreenTitleCategoryAreaBinding
import com.ivanmorgillo.corsoandroid.teamc.databinding.DetailTextInstructionBinding
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teamc.detail.DetailRecipeScreenViewHolder.IngredientsViewHolder
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
import com.ivanmorgillo.corsoandroid.teamc.utils.imageLoader
import com.ivanmorgillo.corsoandroid.teamc.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.math.BigInteger

sealed class DetailScreenItems {
    abstract val id: Long

    data class TitleCategoryArea(val title: String, val category: String, val area: String) : DetailScreenItems() {
        override val id: Long = BigInteger(title.toByteArray()).toLong()
    }

    data class Image(val image: String, val isFavourite: Boolean) : DetailScreenItems() {
        override val id: Long = BigInteger(image.toByteArray()).toLong()
    }

    data class Ingredients(val ingredients: List<IngredientUI>) : DetailScreenItems() {
        override val id: Long
            get() {
                val firstIngredient = ingredients.first()
                return BigInteger(firstIngredient.name.toByteArray()).toLong()
            }
    }

    data class Instructions(val instructions: List<String>) : DetailScreenItems() {
        override val id: Long
            get() {
                val firstInstruction = instructions.first()
                return BigInteger(firstInstruction.toByteArray()).toLong()
            }
    }

    data class VideoInstructions(val videoInstructions: String) : DetailScreenItems() {
        override val id: Long = BigInteger(videoInstructions.toByteArray()).toLong()
    }
}

private const val IMAGE_VIEWTYPE = 1
private const val INGREDIENTS_VIEWTYPE = 2
private const val INSTRUCTIONS_VIEWTYPE = 3
private const val TITLE_CATEGORY_AREA_VIEWTYPE = 4
private const val VIDEOINSTRUCTIONS_VIEWTYPE = 5

class DetailRecipeScreenAdapter(private val onDetailFavouriteClicked: () -> Unit) : RecyclerView.Adapter<DetailRecipeScreenViewHolder>() {
    var items = emptyList<DetailScreenItems>()
        set(value) {
            val diffCallBack = DetailScreeDiffCallBack(field, value)
            val resultDiff = DiffUtil.calculateDiff(diffCallBack)
            field = value
            resultDiff.dispatchUpdatesTo(this)
        }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

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
        val detailBinding = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE_CATEGORY_AREA_VIEWTYPE -> {
                val titleBinding = DetailScreenTitleCategoryAreaBinding.inflate(detailBinding, parent, false)
                TitleCategoryAreaViewHolder(titleBinding)
            }
            IMAGE_VIEWTYPE -> {
                val imageBinding = DetailRecipeScreenImageBinding.inflate(detailBinding, parent, false)
                ImageViewHolder(imageBinding)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val instructionsBinding = DetailRecipeScreenInstructionsBinding.inflate(detailBinding, parent, false)
                InstructionsViewHolder(instructionsBinding)
            }
            VIDEOINSTRUCTIONS_VIEWTYPE -> {
                val videoInstructionsBinding = DetailRecipeScreenVideoInstructionsBinding.inflate(detailBinding, parent, false)
                VideoInstructionsViewHolder(videoInstructionsBinding)
            }
            INGREDIENTS_VIEWTYPE -> {
                val ingredientsBinding = DetailRecipeScreenIngredientsBinding.inflate(detailBinding, parent, false)
                IngredientsViewHolder(ingredientsBinding)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailRecipeScreenViewHolder, position: Int) {
        when (holder) {
            is TitleCategoryAreaViewHolder -> holder.bind(items[position] as TitleCategoryArea)
            is ImageViewHolder -> holder.bind(items[position] as Image, onDetailFavouriteClicked)
            is InstructionsViewHolder -> holder.bind(items[position] as Instructions)
            is VideoInstructionsViewHolder -> holder.bind(items[position] as VideoInstructions)
            is IngredientsViewHolder -> holder.bind(items[position] as Ingredients)
        }.exhaustive
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

sealed class DetailRecipeScreenViewHolder(itemView: View) : ViewHolder(itemView) {
    // 1
    class TitleCategoryAreaViewHolder(private val binding: DetailScreenTitleCategoryAreaBinding) : DetailRecipeScreenViewHolder(binding.root) {
        fun bind(titleCategoryArea: TitleCategoryArea) {
            binding.detailScreenTitle.text = titleCategoryArea.title
            binding.detailRecipeScreenAreaValue.text = titleCategoryArea.area
            binding.detailRecipeScreenCategoryValue.text = titleCategoryArea.category
        }
    }

    class ImageViewHolder(private val binding: DetailRecipeScreenImageBinding) : DetailRecipeScreenViewHolder(binding.root) {
        fun bind(item: Image, onDetailFavouriteClicked: () -> Unit) {
            binding.detailRecipeScreenImage.load(item.image, imageLoader(itemView.context))

            if (item.isFavourite) {
                binding.icon.setImageResource(R.drawable.ic_favourite_list)
            } else {
                binding.icon.setImageResource(R.drawable.ic_favourite_border_list)
            }
            binding.icon.setOnClickListener {
                onDetailFavouriteClicked()
            }
        }
    }

    class IngredientsViewHolder(private val binding: DetailRecipeScreenIngredientsBinding) : DetailRecipeScreenViewHolder(binding.root) {
        fun bind(ingredients: Ingredients) {
            val adapter = IngredientsAdapter()
            binding.detailRecipeScreenIngredients.adapter = adapter
            adapter.items = ingredients.ingredients
        }
    }

    class InstructionsViewHolder(private val binding: DetailRecipeScreenInstructionsBinding) : DetailRecipeScreenViewHolder(binding.root) {
        fun bind(instructions: Instructions) {
            val adapter = InstructionAdapter()
            binding.recipeDetailScreenInstructions.adapter = adapter
            adapter.instructionsList = instructions.instructions
        }
    }

    class VideoInstructionsViewHolder(private val binding: DetailRecipeScreenVideoInstructionsBinding) : DetailRecipeScreenViewHolder(binding.root) {
        fun bind(item: VideoInstructions) {
            if (item.videoInstructions.isEmpty()) {
                binding.youtubePlayerView.gone()
            } else {
                binding.youtubePlayerView.visible()
                binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
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
        val binding = DetailRecipeScreenIngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holderItem: IngredientsItemViewHolder, position: Int) {
        holderItem.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class IngredientsItemViewHolder(private val binding: DetailRecipeScreenIngredientItemBinding) : ViewHolder(binding.root) {
    fun bind(item: IngredientUI) {
        binding.ingredientImage.load("https://www.themealdb.com/images/ingredients/${item.name}-Small.png", imageLoader(itemView.context)) {
            crossfade(true)
        }
        binding.detailScreenIngredientName.text = item.name
        binding.detailScreenIngredientQuantity.text = item.measure
    }
}

class InstructionAdapter : RecyclerView.Adapter<InstructionsItemViewHolder>() {
    var instructionsList: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsItemViewHolder {
        val binding = DetailTextInstructionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstructionsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstructionsItemViewHolder, position: Int) {
        holder.bind(instructionsList[position])
    }

    override fun getItemCount(): Int {
        return instructionsList.size
    }
}

class InstructionsItemViewHolder(private val binding: DetailTextInstructionBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(instruction: String) {
        binding.detailSingleInstruction.text = instruction
    }
}

data class IngredientUI(val name: String, val measure: String)

class DetailScreeDiffCallBack(val oldList: List<DetailScreenItems>, val newList: List<DetailScreenItems>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
