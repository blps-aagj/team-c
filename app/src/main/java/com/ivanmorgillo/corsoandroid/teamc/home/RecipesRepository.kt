package com.ivanmorgillo.corsoandroid.teamc.home

import com.ivanmorgillo.corsoandroid.teamc.domain.Area
import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe
import com.ivanmorgillo.corsoandroid.teamc.domain.RecipeByArea
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadAreaResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.LoadRecipesResult
import com.ivanmorgillo.corsoandroid.teamc.network.home.RecipeAPI

interface RecipesRepository {
    suspend fun loadAllRecipesByArea(forced: Boolean = false): List<RecipeByArea>?
    suspend fun loadRecipes(area: String): LoadRecipesResult
}

// implementa l'interfaccia sopra
class RecipeRepositoryImpl(private val recipeAPI: RecipeAPI) : RecipesRepository {
    override suspend fun loadAllRecipesByArea(forced: Boolean): List<RecipeByArea>? {
        return recipeAPI.loadAreas().map()?.run {
            this.map {
                var recipes = loadRecipes(it.nameArea)
                it to recipes
            }.mapNotNull {
                val area = it.first
                val recipes = it.second.getOrNull()
                if (recipes.isNullOrEmpty()) {
                    null
                } else {
                    RecipeByArea(
                        nameArea = area.nameArea,
                        recipeByArea = recipes
                    )
                }
            }
        }
    }

    override suspend fun loadRecipes(area: String): LoadRecipesResult {
        return recipeAPI.loadRecipes(area)
    }
}

// gestire l'empty anche qui e cambiarlo in getOrNull
fun LoadAreaResult.map(): List<Area>? {
    return when (this) {
        is LoadAreaResult.Failure -> null
        is LoadAreaResult.Success -> this.areas
    }.exhaustive
}

fun LoadRecipesResult.getOrNull(): List<Recipe>? {
    return when (this) {
        is LoadRecipesResult.Failure -> null
        is LoadRecipesResult.Success -> {
            if (this.recipes.isEmpty()) {
                null
            } else {
                this.recipes
            }
        }
    }
}
