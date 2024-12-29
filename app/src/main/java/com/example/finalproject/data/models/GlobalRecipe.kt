package com.example.finalproject.data.models

import com.google.gson.annotations.SerializedName

data class GlobalRecipe(
    val id: Int,
    val title: String,
    val userId : String,       // User ID who uploaded the recipe
    @SerializedName("summary") val description: String? = "", // Replace with the actual field name
    @SerializedName("image") val imageUrl: String? = ""       // Replace with the actual field name
)