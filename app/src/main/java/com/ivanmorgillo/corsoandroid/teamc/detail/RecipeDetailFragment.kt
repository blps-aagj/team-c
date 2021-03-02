package com.ivanmorgillo.corsoandroid.teamc.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.visible
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.no_recipe_found.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val Z_AXIS: Float = 100f

class RecipeDetailFragment : Fragment() {

    private val recipeDetailViewModel: RecipeDetailViewModel by viewModel()

    private val args: RecipeDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    //  Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(
            getView()!!,
            Z_AXIS
        )
        val adapter = DetailRecipeScreenAdapter()
        val recipeId = args.recipeId
        recipes_list_root.adapter = adapter
        no_recipe_found_random_btn.setOnClickListener {
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
                    recipes_list_root.visible()
                    detail_screen_no_recipe.gone()
                    Timber.d("RecipeDetailScreenStates ${state.recipeDetail}")
                    adapter.items = state.recipeDetail
                }
                RecipeDetailScreenStates.Loading -> {
                    // Aggiungiamo una Progress ? (SI)
                    Timber.d("RecipeDetailScreenStates Loading")
                }
                RecipeDetailScreenStates.Error.NoNetwork -> {
                    recipes_list_root.gone()
                    detail_screen_no_network.visible()
                }
                RecipeDetailScreenStates.Error.NoRecipeFound -> {
                    recipes_list_root.gone()
                    detail_screen_no_recipe.visible()
                }
            }.exhaustive
        })

        recipeDetailViewModel.setRecipeId(recipeId)
        recipeDetailViewModel.send(RecipeDetailScreenEvent.OnScreenRecipeDetailReady)
        Timber.d("RecipeDetailFragment/onViewCreated")
    }
}
