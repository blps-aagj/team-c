package com.ivanmorgillo.corsoandroid.teamc.favourite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentFavouriteListBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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
                lifecycleScope.launch { viewModel.send(FavouriteScreenEvents.OnItemSwiped(position)) }
            }
        })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.favouriteRecyclerView)

        viewModel.favouriteStates.observe(viewLifecycleOwner, {
            when (it) {
                is FavouriteScreenStates.FavouriteScreenContent -> adapter.items = it.favouriteUiList
                FavouriteScreenStates.FavouriteScreenError -> TODO()
                FavouriteScreenStates.FavouriteScreenLoading -> TODO()
            }.exhaustive
        })
        viewModel.send(FavouriteScreenEvents.OnFavouriteScreenReady)
    }
}
