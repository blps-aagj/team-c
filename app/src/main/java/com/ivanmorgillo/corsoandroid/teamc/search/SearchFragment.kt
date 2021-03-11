package com.ivanmorgillo.corsoandroid.teamc.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentSearchBinding
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: RecipeSearchViewModel by viewModel()
    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SearchRecipeAdapter()
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    val searchText = binding.searchEditText.text
                    Timber.d("searchText$searchText")
                    viewModel.setRecipeName(searchText.toString())
                    viewModel.send(RecipeSearchScreenEvent.OnRecipeSearch(searchText.toString()))
                    true
                }
                else -> false
            }
        }
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text
            viewModel.setRecipeName(searchText.toString())
            viewModel.send(RecipeSearchScreenEvent.OnRecipeSearch(searchText.toString()))
        }
        binding.searchViewRecipeRecyclerviewId.adapter = adapter

//        binding.searchViewBarId.setOnEditorActionListener(OnEditorActionListener { v, keyAction, keyEvent ->
//            if ( //Soft keyboard search
//                keyAction == EditorInfo.IME_ACTION_SEARCH ||  //Physical keyboard enter key
//                keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.keyCode && keyEvent.action == KeyEvent.ACTION_DOWN
//            ) {
//
//                Toast.makeText(context,v.text.toString() ,Toast.LENGTH_LONG).show();
//
//                return@OnEditorActionListener true
//            }
//            false
//        })
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecipeSearchScreenStates.Content -> {
                    Timber.d("")
                    adapter.items = state.recipe
                }
                RecipeSearchScreenStates.Error.NoNetwork -> TODO()
                RecipeSearchScreenStates.Error.NoRecipeFound -> TODO()
                RecipeSearchScreenStates.Loading -> TODO()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
