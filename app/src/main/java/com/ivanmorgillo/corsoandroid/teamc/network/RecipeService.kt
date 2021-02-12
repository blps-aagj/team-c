package com.ivanmorgillo.corsoandroid.teamc.network

import retrofit2.http.GET

interface RecipeService {
    @GET("filter.php?c=Beef")
    suspend fun loadRecipes(): RecipeDTO
}
