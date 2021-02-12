package com.ivanmorgillo.corsoandroid.teamc.network

import retrofit2.http.GET

interface RecipeService {
    @GET("filter.php")
    suspend fun loadRecipes(): RecipeDTO
}
