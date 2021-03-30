package com.ivanmorgillo.corsoandroid.teamc.favourite

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teamc.MainActivity
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.databinding.FragmentFavouriteListBinding
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamc.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouriteFragment : Fragment(R.layout.fragment_favourite_list) {

    private val viewModel: FavouriteViewModel by viewModel()
    private val binding by viewBinding(FragmentFavouriteListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         val cacheSize = 20
        val adapter = FavouriteRecipeScreenAdapter {
            viewModel.send(FavouriteScreenEvents.OnFavouriteRecipeClick(it))
        }
        binding.favouriteRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.favouriteRecyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
        binding.favouriteRecyclerView.setHasFixedSize(true)
        binding.favouriteRecyclerView.setItemViewCacheSize(cacheSize)
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
                    binding.favouriteInfoMessage.gone()
                    binding.recipesListProgressBar.root.gone()
                    adapter.items = it.favouriteUiList.toMutableList()
                }
                FavouriteScreenStates.FavouriteScreenError -> {
                    Toast.makeText(context, "Aggiungi preferiti per iniziare", Toast.LENGTH_SHORT).show()
                }
                FavouriteScreenStates.FavouriteScreenLoading -> {
                    binding.favouriteInfoMessage.gone()
                    binding.recipesListProgressBar.root.visible()
                }
                FavouriteScreenStates.FavouriteScreenEmpty -> {
                    binding.recipesListProgressBar.root.gone()
                    binding.favouriteRecyclerView.gone()
                    binding.favouriteInfoMessage.visible()
                }
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

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.setCheckedItem(R.id.favourite_list)
    }
}
