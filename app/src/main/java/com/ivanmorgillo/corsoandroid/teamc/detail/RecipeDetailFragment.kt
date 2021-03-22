package com.ivanmorgillo.corsoandroid.teamc.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.StartGoogleSignIn
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentDetailBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val Z_AXIS: Float = 100f

class RecipeDetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel: RecipeDetailViewModel by viewModel()
    private val binding by viewBinding(FragmentDetailBinding::bind)

    private
    val args: RecipeDetailFragmentArgs by navArgs()

    //  Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(
            binding.root,
            Z_AXIS
        )
        val adapter = DetailRecipeScreenAdapter {
            viewModel.send(RecipeDetailScreenEvent.OnFavouriteClicked)
        }
        val recipeId = args.recipeId
        adapter.setHasStableIds(true)
        binding.recipesListRoot.adapter = adapter
        binding.detailScreenNoRecipe.noRecipeFoundRandomBtn.setOnClickListener {
            viewModel.send(RecipeDetailScreenEvent.OnErrorRandomClick)
        }
        if (recipeId == 0L) {
            // Torna nella schermata precedente
            findNavController().popBackStack()
        }
        viewModel.states.observe(viewLifecycleOwner, { state ->
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
                RecipeDetailScreenStates.NoLogged -> showDialog(requireContext())
            }.exhaustive
        })

        viewModel.send(RecipeDetailScreenEvent.OnScreenRecipeDetailReady(recipeId))
    }

    private fun showDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setMessage("Se vuoi aggiungere la ricetta nei preferiti, loggati! :)")
            .setNegativeButton("cancel") { _, _ ->
                // Respond to neutral button press
            }
            .setPositiveButton("Login") { _, _ ->
                viewModel.send(RecipeDetailScreenEvent.OnLoginDialogClick)
                (activity as StartGoogleSignIn).startGoogleSignIn { Log.d("msg", "Login successful") }
            }
            .show()
    }
}
