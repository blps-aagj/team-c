package com.ivanmorgillo.corsoandroid.teamc.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentDetailBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val Z_AXIS: Float = 100f

class RecipeDetailFragment : Fragment(R.layout.fragment_detail) {

    private val recipeDetailViewModel: RecipeDetailViewModel by viewModel()
    private val binding by viewBinding(FragmentDetailBinding::bind)

    private val args: RecipeDetailFragmentArgs by navArgs()

    //  Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(
            binding.root,
            Z_AXIS
        )
        val adapter = DetailRecipeScreenAdapter {
            recipeDetailViewModel.send(RecipeDetailScreenEvent.OnFavouriteClicked)
        }
        val recipeId = args.recipeId
        adapter.setHasStableIds(true)
        binding.recipesListRoot.adapter = adapter
        binding.detailScreenNoRecipe.noRecipeFoundRandomBtn.setOnClickListener {
            recipeDetailViewModel.send(RecipeDetailScreenEvent.OnErrorRandomClick)
        }
        if (recipeId == 0L) {
            // Torna nella schermata precedente
            findNavController().popBackStack()
        }
        recipeDetailViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeDetailScreenStates.Content -> {
                    binding.detailScreenProgressBar.root.gone()
                    binding.recipesListRoot.visible()
                    binding.detailScreenNoRecipe.root.gone()
                    adapter.items = state.recipeDetail
                }
                RecipeDetailScreenStates.Loading -> {
                    binding.recipesListRoot.gone()
                    binding.detailScreenProgressBar.root.visible()
                }
                RecipeDetailScreenStates.Error.NoNetwork -> {
                    binding.detailScreenProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.detailScreenNoNetwork.root.visible()
                }
                RecipeDetailScreenStates.Error.NoRecipeFound -> {
                    binding.detailScreenProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.detailScreenNoRecipe.root.visible()
                }
            }.exhaustive
        })

        recipeDetailViewModel.send(RecipeDetailScreenEvent.OnScreenRecipeDetailReady(recipeId))
    }
}
