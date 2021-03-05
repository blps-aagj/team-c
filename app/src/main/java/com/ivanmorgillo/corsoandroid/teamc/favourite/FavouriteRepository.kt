package com.ivanmorgillo.corsoandroid.teamc.favourite

import com.ivanmorgillo.corsoandroid.teamc.home.RecipeUI

interface FavouriteRepository {
    suspend fun loadFavourites(): List<RecipeUI>
    suspend fun save(recipe: RecipeUI, isFavourite: Boolean)
    fun delete(position: Int)
}

class FavouriteRepositoryImpl : FavouriteRepository {
    private val favouriteListID: MutableList<RecipeUI> = mutableListOf()
    override suspend fun loadFavourites(): List<RecipeUI> {
        return favouriteListID
    }

    override suspend fun save(recipe: RecipeUI, isFavourite: Boolean) {
        if (isFavourite) {
            favouriteListID.add(recipe)
        } else {
            favouriteListID.remove(recipe)
        }
    }

    override fun delete(position: Int) {
        favouriteListID.removeAt(position)
    }
}
