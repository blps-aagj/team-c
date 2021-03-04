package com.ivanmorgillo.corsoandroid.teamc.network.detail

import com.ivanmorgillo.corsoandroid.teamc.domain.Ingredient

fun getVideoID(videoUri: String?): String {
    return if (videoUri != null && videoUri.isNotBlank()) {
        videoUri.replace("https://www.youtube.com/watch?v=", "")
    } else {
        ""
    }
}

fun loadRecipeInstruction(instructions: String): List<String> {
    return instructions.split("/r/n")
}

fun validateIngredientsAndMeasures(ingredientName: String?, ingredientQuantity: String?): Ingredient? {
    // inserire check per vedere se la stringa è fatta solo di numeri
    return if (ingredientName.isNullOrBlank()) {
        null
    } else {
        if (ingredientQuantity.isNullOrBlank()) {
            Ingredient(ingredientName, "q.s.")
        } else {
            Ingredient(ingredientName, ingredientQuantity)
        }
    }
}
