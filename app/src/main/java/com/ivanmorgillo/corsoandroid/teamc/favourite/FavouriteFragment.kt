package com.ivanmorgillo.corsoandroid.teamc.favourite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentFavouriteListBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouriteFragment : Fragment(R.layout.fragment_favourite_list) {

    private val viewModel: FavouriteViewModel by viewModel()
    private val binding by viewBinding(FragmentFavouriteListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavouriteRecipeScreenAdapter {
            viewModel.send(FavouriteScreenEvents.OnFavouriteRecipeClick(it))
        }
        binding.favouriteRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.favouriteRecyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
        binding.favouriteRecyclerView.adapter = adapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, object :
            RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
                viewModel.send(FavouriteScreenEvents.OnItemSwiped(position))
            }
        })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.favouriteRecyclerView)

        viewModel.favouriteStates.observe(viewLifecycleOwner, {
            when (it) {
                is FavouriteScreenStates.FavouriteScreenContent -> {
                    adapter.items = it.favouriteUiList.toMutableList()
                }
                FavouriteScreenStates.FavouriteScreenError -> TODO()
                FavouriteScreenStates.FavouriteScreenLoading -> TODO()
            }.exhaustive
        })
        viewModel.favouriteActions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is FavouriteScreenAction.NavigateToDetailFromFavourite -> {
                    val directions = FavouriteFragmentDirections.actionFavouriteFragmentToDetailFragment(action.recipe.idRecipe)
                    Timber.d("Invio al detail RecipeId = ${action.recipe.idRecipe}")
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
        viewModel.send(FavouriteScreenEvents.OnFavouriteScreenReady)
    }
}
