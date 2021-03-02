package com.ivanmorgillo.corsoandroid.teamc.network.home

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AreaDTO(
    @SerializedName("meals")
    val areas: List<Area>
) {
    @Keep
    data class Area(
        @SerializedName("strArea")
        val strArea: String
    )
}
