package com.ivanmorgillo.corsoandroid.teamc.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnFavouriteClicked
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnRefreshClick
import com.ivanmorgillo.corsoandroid.teamc.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamc.MainScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamc.MainViewModel
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentHomeBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.home.HomeFragmentDirections.Companion.actionHomeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: MainViewModel by viewModel()
    private val binding by viewBinding(FragmentHomeBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val adapter = RecipeByAreaAdapter(
            { viewModel.send(OnRecipeClick(it)) },
            { viewModel.send((OnFavouriteClicked(it))) }
        )
        binding.recipesList.adapter = adapter
        binding.randomBtnFloating.setOnClickListener {
            viewModel.send(MainScreenEvent.OnRandomClick)
        }
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is Content -> setupContent(adapter, state)
                MainScreenStates.Loading -> {
                    binding.recipesListProgressBar.visible()
                    Timber.d("MainscreenStates Loading")
                }
                MainScreenStates.Error.NoNetwork -> {
                    binding.recipesListProgressBar.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoNetwork.root.visible()
                }
                MainScreenStates.Error.NoRecipeFound -> {
                    binding.recipesListProgressBar.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoRecipe.root.visible()
                }
            }.exhaustive
        })
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is NavigateToDetail -> {
                    val directions = actionHomeFragmentToDetailFragment(action.recipe.id)
                    Timber.d("Invio al detail RecipeId = ${action.recipe.id}")
                    findNavController().navigate(directions)
                }
                is MainScreenAction.NavigateToDetailRandom -> {

                    val recipeId = action.recipe.recipeId.toLongOrNull()
                    if (recipeId == null) {
                        binding.recipesListProgressBar.gone()
                        binding.recipesListRoot.gone()
                        binding.mainScreenNoRecipe.root.visible()
                    } else {
                        val directions = actionHomeFragmentToDetailFragment(recipeId) // Da vedere se arriva null
                        Timber.d("Invio al detail RecipeId = ${action.recipe.recipeId}")
                        findNavController().navigate(directions)
                    }
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnReady)
        Timber.d("Wow")
    }

    private fun setupContent(adapter: RecipeByAreaAdapter, state: Content) {
        binding.recipesListProgressBar.gone()
        binding.mainScreenNoNetwork.root.gone()
        binding.recipesListRoot.visible()
        adapter.setRecipesByArea(state.recipes)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.utils_menu, menu)
        Timber.d("onCreateOptionsMenu ${R.menu.utils_menu}, $menu")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh_btn) viewModel.send(OnRefreshClick)
        else if (item.itemId == R.id.random_btn) viewModel.send(MainScreenEvent.OnRandomClick)
        return super.onOptionsItemSelected(item)
    }
}
