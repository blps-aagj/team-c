package com.ivanmorgillo.corsoandroid.teamc.detail.network

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeDetailService {
    @GET("lookup.php")
    suspend fun loadDetailsRecipe(@Query("i") id: String): RecipeDetailDTO
}
