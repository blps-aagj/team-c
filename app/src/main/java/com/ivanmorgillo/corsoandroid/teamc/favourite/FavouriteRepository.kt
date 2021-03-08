package com.ivanmorgillo.corsoandroid.teamc.favourite

import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe

interface FavouriteRepository {
    suspend fun loadFavourites(): List<Recipe>
    suspend fun save(recipe: Recipe, isFavourite: Boolean)
    fun delete(position: Int)
}

class FavouriteRepositoryImpl : FavouriteRepository {
    private val favouriteListID: MutableList<Recipe> = mutableListOf()
    override suspend fun loadFavourites(): List<Recipe> {
        return favouriteListID
    }

    override suspend fun save(recipe: Recipe, isFavourite: Boolean) {
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
