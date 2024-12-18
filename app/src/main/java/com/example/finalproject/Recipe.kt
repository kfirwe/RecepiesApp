package com.example.finalproject

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    val results: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    @SerializedName("summary") val description: String? = "", // Replace with the actual field name
    @SerializedName("image") val imageUrl: String? = ""       // Replace with the actual field name
)