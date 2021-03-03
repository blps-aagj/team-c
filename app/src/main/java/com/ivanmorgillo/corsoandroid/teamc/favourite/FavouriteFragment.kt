package com.ivanmorgillo.corsoandroid.teamc.favourite

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import kotlinx.android.synthetic.main.fragment_favourite_list.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment : Fragment() {

    private val viewModel: FavouriteViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favourite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavouriteRecipeScreenAdapter {
            viewModel.send(FavouriteScreenEvents.OnFavouriteRecipeClick(it))
        }

        favourite_recycler_view.itemAnimator = DefaultItemAnimator()
        favourite_recycler_view.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
        favourite_recycler_view.adapter = adapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, object :
            RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
                lifecycleScope.launch {
                    viewModel.send(FavouriteScreenEvents.OnItemSwiped(position))
                    adapter.items.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
            }
        })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(favourite_recycler_view)

        viewModel.favouriteStates.observe(viewLifecycleOwner, {
            when (it) {
                is FavouriteScreenStates.FavouriteScreenContent -> {
                    adapter.items = it.favouriteUiList.toMutableList()
                }
                FavouriteScreenStates.FavouriteScreenError -> TODO()
                FavouriteScreenStates.FavouriteScreenLoading -> TODO()
            }.exhaustive
        })
        viewModel.send(FavouriteScreenEvents.OnFavouriteScreenReady)
    }
}

class RecyclerItemTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: RecyclerItemTouchHelperListener
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView: View = (viewHolder as FavouriteViewHolder).binding.favoriteRecipeForeground
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as FavouriteViewHolder).binding.favoriteRecipeForeground
        getDefaultUIUtil().onDrawOver(
            c,
            recyclerView,
            foregroundView,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = (viewHolder as FavouriteViewHolder).binding.favoriteRecipeForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as FavouriteViewHolder).binding.favoriteRecipeForeground
        getDefaultUIUtil()
            .onDraw(
                c,
                recyclerView,
                foregroundView,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int)
    }
}
