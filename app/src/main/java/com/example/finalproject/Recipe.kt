package com.example.finalproject

import com.google.gson.annotations.SerializedName
import com.google.firebase.Timestamp

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

data class UserProfile(
    val displayName: String? = null,
    val bio: String? = null,
    val profilePictureBase64: String? = null // Updated to store the image as a Base64 string
)


data class Recipe(
    val id: String = "",               // Default empty string
    val title: String = "",            // Default empty string
    val userId: String = "",           // Default empty string
    val description: String? = null,   // Nullable with default null
    val imageBase64: String? = null,   // Nullable with default null
    val comments: List<Map<String, Any>> = emptyList() // Default empty list
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", null, null, emptyList())
}

data class Comment(
    val userName: String = "",
    val commentText: String = "",
    val rating: Int = 0,
    val timestamp: Timestamp = Timestamp.now(), // Firestore Timestamp
    val id: String = "" // Unique comment ID for updates/deletes
)

data class GlobalRecipe(
    val id: Int,
    val title: String,
    val userId : String,       // User ID who uploaded the recipe
    @SerializedName("summary") val description: String? = "", // Replace with the actual field name
    @SerializedName("image") val imageUrl: String? = ""       // Replace with the actual field name
)