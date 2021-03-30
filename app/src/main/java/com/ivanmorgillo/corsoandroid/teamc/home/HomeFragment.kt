package com.ivanmorgillo.corsoandroid.teamc.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.ivanmorgillo.corsoandroid.teamc.MainActivity
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.StartGoogleSignIn
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentHomeBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.home.HomeFragmentDirections.Companion.actionHomeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamc.home.HomeFragmentDirections.Companion.actionHomeFragmentToSearchFragment
import com.ivanmorgillo.corsoandroid.teamc.home.HomeScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamc.home.HomeScreenEvent.OnFavouriteClicked
import com.ivanmorgillo.corsoandroid.teamc.home.HomeScreenEvent.OnRecipeClick
import com.ivanmorgillo.corsoandroid.teamc.home.HomeScreenEvent.OnRefreshClick
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModel()
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private var selectedTab: String = "Nation"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val adapter = RecipeByTabAdapter(
            { viewModel.send(OnRecipeClick(it)) },
            { viewModel.send((OnFavouriteClicked(it))) }
        )
        binding.recipesList.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val contentDescription = tab?.contentDescription
                val nationTab = resources.getString(R.string.nazione)
                val categoryTab = resources.getString(R.string.categoria)
                when (contentDescription) {
                    nationTab -> {
                        selectedTab = contentDescription.toString()
                        viewModel.send(HomeScreenEvent.OnClickedNation)
                    }
                    categoryTab -> {
                        selectedTab = contentDescription.toString()
                        viewModel.send(HomeScreenEvent.OnClickedCategory)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        states(adapter)
        actions()
        viewModel.send(HomeScreenEvent.OnReady)
    }

    private fun actions() {

        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is NavigateToDetail -> {
                    val directions = actionHomeFragmentToDetailFragment(action.recipe.id)
                    Timber.d("Invio al detail RecipeId = ${action.recipe.id}")
                    findNavController().navigate(directions)
                }
                is HomeScreenAction.NavigateToDetailRandom -> {
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
                HomeScreenAction.NavigateToSearch -> {
                    val directions = actionHomeFragmentToSearchFragment()
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }

    private fun states(adapter: RecipeByTabAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is HomeScreenStates.Content -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.mainScreenNoNetwork.root.gone()
                    binding.recipesListRoot.visible()
                    adapter.recipeByTab = state.recipes
                }
                HomeScreenStates.Loading -> {
                    binding.recipesListProgressBar.root.visible()
                    Timber.d("MainScreenStates Loading")
                }
                HomeScreenStates.Error.NoNetwork -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoNetwork.root.visible()
                }
                HomeScreenStates.Error.NoRecipeFound -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.recipesListRoot.gone()
                    binding.mainScreenNoRecipe.root.visible()
                }
                HomeScreenStates.NoLogged -> showDialog(requireContext())
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
            R.id.refresh_btn -> viewModel.send(OnRefreshClick(selectedTab))
            R.id.random_btn -> viewModel.send(HomeScreenEvent.OnRandomClick)
            R.id.search_btn -> viewModel.send(HomeScreenEvent.OnSearchClick)
            else -> error("Home onOptionsItemSelected")
        }.exhaustive

        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setMessage("Se vuoi aggiungere la ricetta nei preferiti, loggati! :)")
            .setNegativeButton("cancel") { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton("Login") { dialog, which ->
                viewModel.send(HomeScreenEvent.OnLoginDialogClick)
                (activity as StartGoogleSignIn).startGoogleSignIn { Log.d("msg", "Login successful") }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.setCheckedItem(R.id.home_page)
    }
}
