package com.ivanmorgillo.corsoandroid.teamc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = RecipesAdapter()

        // dobbiamo mettere l'adapter in comunicazione con la recyclerview
        recipes_list.adapter = adapter

        val recipeList = viewModel.getRecipes()
        // ora passiamo la lista all'adapter
        adapter.setRecipies(recipeList)
    }
}

// mappa l'oggetto che sta nella card
data class RecipeUI(
    val recipeName: String,
    val recipeImageUrl: String
)


