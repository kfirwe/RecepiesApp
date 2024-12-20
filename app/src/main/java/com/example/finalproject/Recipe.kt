package com.example.finalproject

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    val results: List<Recipe>?,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

data class IngredientResponse(
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val amount: Amount,
    val image: String
)

data class Amount(
    val metric: Metric,
    val us: US
)

data class Metric(
    val value: Double,
    val unit: String
)

data class US(
    val value: Double,
    val unit: String
)

data class UserProfile(
    val displayName: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null
)


data class Recipe(
    val id: Int,
    val title: String,
    val userId : String,       // User ID who uploaded the recipe
    @SerializedName("summary") val description: String? = "", // Replace with the actual field name
    @SerializedName("image") val imageUrl: String? = ""       // Replace with the actual field name
)