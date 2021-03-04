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
import timber.log.Timber

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
        val adapter = DetailRecipeScreenAdapter()
        val recipeId = args.recipeId
        binding.recipesListRoot.adapter = adapter
        binding.detailScreenNoRecipe.noRecipeFoundRandomBtn.setOnClickListener {
            Timber.d("setOnClickListener random")
            recipeDetailViewModel.send(RecipeDetailScreenEvent.OnScreenRecipeDetailRandomReady)
        }
        if (recipeId == 0L) {
            // Torna nella schermata precedente
            findNavController().popBackStack()
        } else {
            Timber.d("RecipeId = $recipeId")
        }
        recipeDetailViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeDetailScreenStates.Content -> {
                    binding.recipesListRoot.visible()
                    binding.detailScreenNoRecipe.root.gone()
                    Timber.d("RecipeDetailScreenStates ${state.recipeDetail}")
                    adapter.items = state.recipeDetail
                }
                RecipeDetailScreenStates.Loading -> {
                    // Aggiungiamo una Progress ? (SI)
                    Timber.d("RecipeDetailScreenStates Loading")
                }
                RecipeDetailScreenStates.Error.NoNetwork -> {
                    binding.recipesListRoot.gone()
                    binding.detailScreenNoNetwork.root.visible()
                }
                RecipeDetailScreenStates.Error.NoRecipeFound -> {
                    binding.recipesListRoot.gone()
                    binding.detailScreenNoRecipe.root.visible()
                }
            }.exhaustive
        })

        recipeDetailViewModel.setRecipeId(recipeId)
        recipeDetailViewModel.send(RecipeDetailScreenEvent.OnScreenRecipeDetailReady)
        Timber.d("RecipeDetailFragment/onViewCreated")
    }
}
