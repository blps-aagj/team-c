package com.ivanmorgillo.corsoandroid.teamc.network.home

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("filter.php")
    suspend fun loadRecipes(@Query("a") area: String): RecipeDTO

    @GET("list.php?a=list")
    suspend fun loadArea(): AreaDTO
}
