package com.blps.aagj.cookbook.networking.home

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class CategoryDTO(
    @SerializedName("meals")
    val categories: List<Category>
) {
    @Keep
    data class Category(
        @SerializedName("strCategory")
        val strCategory: String
    )
}
