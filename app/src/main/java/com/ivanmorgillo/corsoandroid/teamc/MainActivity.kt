package com.ivanmorgillo.corsoandroid.teamc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.no_network.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.Menu

import android.view.MenuInflater




class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)//eseguita 1 sola volta

        val adapter = RecipesAdapter {
            viewModel.send(MainScreenEvent.OnRecipeClick(it))
        }

        // dobbiamo mettere l'adapter in comunicazione con la recyclerview
        recipes_list.adapter = adapter
        // osserva in che stato si trova la schermata
        viewModel.states.observe(this, { state ->
            when (state) {
                // Nasconde la progressBar e fa vedere la lista delle ricette
                is MainScreenStates.Content -> {
                    recipes_list_progressBar.gone()
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
            }
        })
        viewModel.actions.observe(this, { action ->
            when (action) {
                is MainScreenAction.NavigateToDetail -> {
                    Toast.makeText(this, "Working in progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenAction.ShowNoInternetMessage -> {
                    recipes_list_progressBar.gone()
                    recipes_list_root.gone()
                    setContentView(R.layout.no_network)
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.send(MainScreenEvent.OnReady)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.refresh , menu)
        return true
    }
}
