package com.ivanmorgillo.corsoandroid.teamc.domain

// lista di ricette: lista di oggetti con nome, immagine e id
data class Recipe(val name: String, val image: String, val idMeal: Long)
data class Area(val nameArea: String)
data class RecipeByArea(val nameArea: String, val recipeByArea: List<Recipe>)
