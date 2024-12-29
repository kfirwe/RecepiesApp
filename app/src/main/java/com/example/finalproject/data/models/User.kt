package com.example.finalproject.data.models

data class UserProfile(
    val displayName: String? = null,
    val bio: String? = null,
    val profilePictureBase64: String? = null // Updated to store the image as a Base64 string
)

data class RecipeResponse(
    val results: List<GlobalRecipe>?,
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

data class User(
    val displayName: String? = null,
    val bio: String? = null,
    val profilePictureBase64: String? = null // Updated to store the image as a Base64 string
)