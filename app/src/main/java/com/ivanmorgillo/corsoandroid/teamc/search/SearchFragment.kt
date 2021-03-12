package com.ivanmorgillo.corsoandroid.teamc.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentSearchBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.search.SearchFragmentDirections.Companion.actionSearchFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: RecipeSearchViewModel by viewModel()
    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SearchRecipeAdapter { viewModel.send(RecipeSearchScreenEvent.OnRecipeClickSearched(it)) }
        states(adapter)
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            Timber.d("EditorInfo $actionId")
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {//3
                    searchRecipes()
                    true
                }
                else -> false
            }
        }
        binding.searchButton.setOnClickListener {
            searchRecipes()
        }

        binding.searchViewRecipeRecyclerviewId.adapter = adapter
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is RecipeSearchScreenAction.NavigateToDetailFromSearch -> {
                    Timber.d("navigate to detail ")
                    val directions = actionSearchFragmentToDetailFragment(action.recipe.id)
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }

    private fun searchRecipes() {
        val searchText = binding.searchEditText.text
        viewModel.send(RecipeSearchScreenEvent.OnReady(searchText.toString()))
        val imm: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun states(adapter: SearchRecipeAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeSearchScreenStates.Content -> {
                    binding.searchRecipeNotFoundText.gone()
                    binding.searchScreenNoNetwork.root.gone()
                    binding.recipesListProgressBar.root.gone()
                    Timber.d("")
                    adapter.items = state.recipe
                }
                RecipeSearchScreenStates.Error.NoNetwork -> {

                    binding.recipesListProgressBar.root.gone()
                    binding.searchRecipeNotFoundText.gone()
                    binding.searchViewRecipeRecyclerviewId.gone()
                    binding.searchScreenNoNetwork.root.visible()
                }
                RecipeSearchScreenStates.Error.NoRecipeFound -> {
                    binding.searchRecipeNotFoundText.visible()
                    binding.searchScreenNoNetwork.root.gone()
                    binding.searchViewRecipeRecyclerviewId.gone()
                }
                RecipeSearchScreenStates.Loading -> binding.recipesListProgressBar.root.visible()
            }.exhaustive
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
