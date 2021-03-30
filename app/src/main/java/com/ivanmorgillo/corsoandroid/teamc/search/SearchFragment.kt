package com.ivanmorgillo.corsoandroid.teamc.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentSearchBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.search.RecipeSearchScreenEvent.OnSearchButtonClick
import com.ivanmorgillo.corsoandroid.teamc.search.RecipeSearchScreenEvent.OnSearchKeyboardClick
import com.ivanmorgillo.corsoandroid.teamc.search.SearchFragmentDirections.Companion.actionSearchFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel: RecipeSearchViewModel by viewModel()
    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val args: SearchFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipName = binding.chipRecipeName
        val chipIngredient = binding.chipIngredient
        var chipNameIsCheck = false
        var chipIngredientIsCheck = false

        chipName.setOnClickListener {
            chipNameIsCheck = chipName.isChecked
        }
        chipIngredient.setOnClickListener {
            chipIngredientIsCheck = chipIngredient.isChecked
        }
        val recipeIngredient = args.recipeIngredient
        if (recipeIngredient.isNotEmpty()) {
            viewModel.send(RecipeSearchScreenEvent.OnSearchIngredientButtonClick(recipeIngredient))
        }
        val adapter = SearchRecipeAdapter { viewModel.send(RecipeSearchScreenEvent.OnRecipeClickSearched(it)) }
        states(adapter)
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            Timber.d("EditorInfo $actionId")
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    closeKeyboard()
                    val searchText = binding.searchEditText.text.toString().trim()
                    viewModel.send(OnSearchKeyboardClick(searchText))
                    true
                }
                else -> false
            }
        }
        binding.searchButton.setOnClickListener {
            closeKeyboard()
            val searchText = binding.searchEditText.text.toString().trim()
            if (chipNameIsCheck && chipIngredientIsCheck) {
                viewModel.send(OnSearchButtonClick(searchText, searchText))
            } else if (chipIngredientIsCheck) {
                viewModel.send(RecipeSearchScreenEvent.OnSearchIngredientButtonClick(searchText))
            } else if (chipNameIsCheck) {
                viewModel.send(RecipeSearchScreenEvent.OnSearchByRecipeNameButtonClick(searchText))
            } else {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchViewRecipeRecyclerviewId.adapter = adapter
        actions()
        binding.searchScreenNoRecipe.noRecipeFoundRandomBtn.setOnClickListener {
            viewModel.send(RecipeSearchScreenEvent.OnErrorRandomClick)
        }
    }

    private fun actions() {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is RecipeSearchScreenAction.NavigateToDetailFromSearch -> {
                    Timber.d("navigate to detail ")
                    val directions = actionSearchFragmentToDetailFragment(action.recipe.id)
                    findNavController().navigate(directions)
                }
                is RecipeSearchScreenAction.NavigateToDetailRandom -> {
                    val recipeId = action.recipeDetail.recipeId.toLongOrNull()
                    if (recipeId == null) {
                        binding.recipesListProgressBar.root.gone()
                        binding.searchScreenNoRecipe.root.visible()
                    } else {
                        val directions = actionSearchFragmentToDetailFragment(recipeId)
                        Timber.d("Invio al detail RecipeId = ${action.recipeDetail.recipeId}")
                        findNavController().navigate(directions)
                    }
                }
            }.exhaustive
        })
    }

    private fun closeKeyboard() {
        val imm: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun states(adapter: SearchRecipeAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeSearchScreenStates.Content -> {
                    binding.searchScreenNoRecipe.root.gone()
                    binding.searchMessageInfoTextView.gone()
                    binding.searchScreenNoNetwork.root.gone()
                    binding.recipesListProgressBar.root.gone()
                    binding.searchViewRecipeRecyclerviewId.visible()
                    adapter.items = state.recipe
                }
                RecipeSearchScreenStates.Error.NoNetwork -> {
                    binding.searchScreenNoRecipe.root.gone()
                    binding.recipesListProgressBar.root.gone()
                    binding.searchMessageInfoTextView.gone()
                    binding.searchViewRecipeRecyclerviewId.gone()
                    binding.searchScreenNoNetwork.root.visible()
                }
                RecipeSearchScreenStates.Error.NoRecipeFound -> {
                    binding.searchScreenNoNetwork.root.gone()
                    binding.searchViewRecipeRecyclerviewId.gone()
                    binding.searchMessageInfoTextView.gone()
                    binding.recipesListProgressBar.root.gone()
                    binding.searchScreenNoRecipe.root.visible()
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
