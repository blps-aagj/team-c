package com.ivanmorgillo.corsoandroid.teamc.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ivanmorgillo.corsoandroid.teamc.MainScreenAction
import com.ivanmorgillo.corsoandroid.teamc.MainScreenEvent
import com.ivanmorgillo.corsoandroid.teamc.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamc.MainViewModel
import com.ivanmorgillo.corsoandroid.teamc.R
import com.ivanmorgillo.corsoandroid.teamc.RecipesAdapter
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.gone
import com.ivanmorgillo.corsoandroid.teamc.visible
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    //  Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RecipesAdapter {
            viewModel.send(MainScreenEvent.OnRecipeClick(it))
        }

        // dobbiamo mettere l'adapter in comunicazione con la recyclerview
        recipes_list.adapter = adapter
        // osserva in che stato si trova la schermata
        viewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                // Nasconde la progressBar e fa vedere la lista delle ricette
                is MainScreenStates.Content -> {
                    recipes_list_progressBar.gone()
                    main_screen_no_network.gone()
                    recipes_list_root.visible()
                    adapter.setRecipes(state.recipes)
                }
                // Mostra Errore
                MainScreenStates.Error -> {
                    recipes_list_progressBar.gone()
                    Snackbar.make(recipes_list, getString(R.string.main_screen_error), Snackbar.LENGTH_LONG)
                        .show()
                }
                // ProgressBar visible
                MainScreenStates.Loading -> {
                    recipes_list_progressBar.visible()
                }
            }.exhaustive
        })
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is MainScreenAction.NavigateToDetail -> {
                    Toast.makeText(view.context, "Working in progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenAction.ShowNoInternetMessage -> {
                    recipes_list_root.gone()
                    main_screen_no_network.visible()
                    Toast.makeText(view.context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnReady)
        Timber.d("Wow")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.refresh, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_btn -> viewModel.send(MainScreenEvent.OnRefreshClick)
            else -> Toast.makeText(context!!, "come se fosse Antani", Toast.LENGTH_SHORT).show()
        }.exhaustive
        return super.onOptionsItemSelected(item)
    }
}
