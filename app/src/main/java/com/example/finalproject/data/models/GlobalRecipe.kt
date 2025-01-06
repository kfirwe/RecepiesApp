package com.example.finalproject.data.models

import com.google.gson.annotations.SerializedName

data class GlobalRecipe(
    val id: Int,
    val title: String,
    val userId: String? = null, // Make userId nullable with a default value of null
    @SerializedName("summary") val description: String? = "", // Replace with the actual field name
    @SerializedName("image") val imageUrl: String? = ""       // Replace with the actual field name
)