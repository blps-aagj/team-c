package com.blps.aagj.cookbook.networking

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.ivanmorgillo.corsoandroid.teamc.domain.Recipe

@Keep
data class RecipeDTO(
    @SerializedName("meals")
    val meals: List<Meal>
) {
    @Keep
    data class Meal(
        @SerializedName("idMeal")
        val idMeal: String,
        @SerializedName("strMeal")
        val strMeal: String,
        @SerializedName("strMealThumb")
        val strMealThumb: String
    )
}

fun RecipeDTO.Meal.toDomain(): Recipe? {
    val id = idMeal.toLongOrNull()
    return if (id != null) {
        Recipe(
            name = strMeal,
            image = strMealThumb,
            idMeal = id
        )
    } else {
        null
    }
}
