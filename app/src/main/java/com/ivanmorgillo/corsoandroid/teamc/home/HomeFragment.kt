package com.ivanmorgillo.corsoandroid.teamc.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamc.MainActivity
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnFavouriteClicked
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent.OnRefreshClick
import com.ivanmorgillo.corsoandroid.teamc.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamc.MainViewModel
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentHomeBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.home.HomeFragmentDirections.Companion.actionHomeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamc.home.HomeFragmentDirections.Companion.actionHomeFragmentToSearchFragment
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
        states(adapter)
        actions()
        viewModel.send(MainScreenEvent.OnReady)
    }

    private fun actions() {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is NavigateToDetail -> {
                    val directions = actionHomeFragmentToDetailFragment(action.recipe.id)
                    Timber.d("Invio al detail RecipeId = ${action.recipe.id}")
                    findNavController().navigate(directions)
                }
                is MainScreenAction.NavigateToDetailRandom -> {
                    // gestire con NavigateToDetail
                    val recipeId = action.recipe.recipeId.toLongOrNull()
                    if (recipeId == null) {
                        binding.recipesListProgressBar.root.gone()
                        binding.recipesListRoot.gone()
                        binding.mainScreenNoRecipe.root.visible()
                    } else {
                        val directions = actionHomeFragmentToDetailFragment(recipeId)
                        Timber.d("Invio al detail RecipeId = ${action.recipe.recipeId}")
                        findNavController().navigate(directions)
                    }
                }
                MainScreenAction.NavigateToSearch -> {
                    val directions = actionHomeFragmentToSearchFragment()
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }

    private fun states(adapter: RecipeByAreaAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is MainScreenStates.Content -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.mainScreenNoNetwork.root.gone()
                    binding.recipesListRoot.visible()
                    adapter.recipeByArea = state.recipes
                }
                MainScreenStates.Loading -> {
                    binding.recipesListProgressBar.root.visible()
                    Timber.d("MainscreenStates Loading")
                }
                MainScreenStates.Error.NoNetwork -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoNetwork.root.visible()
                }
                MainScreenStates.Error.NoRecipeFound -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoRecipe.root.visible()
                }
            }.exhaustive
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.utils_menu, menu)
        Timber.d("onCreateOptionsMenu ${R.menu.utils_menu}, $menu")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_btn -> viewModel.send(OnRefreshClick)
            R.id.random_btn -> viewModel.send(MainScreenEvent.OnRandomClick)
            R.id.search_btn -> viewModel.send(MainScreenEvent.OnSearchClick)
            else -> error("Home onOptionsItemSelected")
        }.exhaustive

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.setCheckedItem(R.id.home_page)
    }
}
