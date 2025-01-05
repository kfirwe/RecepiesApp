package com.example.finalproject.utils

import com.example.finalproject.data.models.Recipe
import com.example.finalproject.database.entities.RecipeEntity

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        userId = userId,
        title = title,
        description = description,
        imageBase64 = null // Default to null
    )
}
