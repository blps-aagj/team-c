package com.blps.aagj.cookbook.networking

import com.blps.aagj.cookbook.networking.detail.RecipeDetailDTO
import com.blps.aagj.cookbook.networking.home.AreaDTO
import com.blps.aagj.cookbook.networking.home.RecipeDTO
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

    @GET("search.php")
    suspend fun loadRecipeSearchByName(@Query("s") name: String): RecipeDetailDTO
}
