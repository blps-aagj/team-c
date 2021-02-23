package com.ivanmorgillo.corsoandroid.teamc.network

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AreaDTO(
    @SerializedName("meals")
    val meals: List<Meal>
) {
    @Keep
    data class Meal(
        @SerializedName("strArea")
        val strArea: String
    )
}
