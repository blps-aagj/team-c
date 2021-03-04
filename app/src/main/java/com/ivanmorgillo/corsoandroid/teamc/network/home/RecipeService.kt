package com.ivanmorgillo.corsoandroid.teamc.network.home

import com.ivanmorgillo.corsoandroid.teamc.network.detail.RecipeDetailDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("filter.php")
    suspend fun loadRecipes(@Query("a") area: String): RecipeDTO

    @GET("list.php?a=list")
    suspend fun loadAreas(): AreaDTO

    @GET("lookup.php")
    suspend fun loadDetailsRecipe(@Query("i") id: String): RecipeDetailDTO

    @GET("random.php")
    suspend fun loadDetailsRecipeRandom(): RecipeDetailDTO
}
