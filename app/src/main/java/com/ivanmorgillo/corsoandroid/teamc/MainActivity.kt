package com.ivanmorgillo.corsoandroid.teamc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = RecipesAdapter()

        // dobbiamo mettere l'adapter in comunicazione con la recyclerview
        recipies_list.adapter = adapter

        // ora passiamo la lista all'adapter
        adapter.setRecipies(recipeList)
    }
}

// mappa l'oggetto che sta nella card
data class RecipeUI(
    val recipeName: String,
    val recipeImageUrl: String
)

val recipeList = listOf<RecipeUI>(
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(recipeName = "Beef and Mustard pie", recipeImageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
)

