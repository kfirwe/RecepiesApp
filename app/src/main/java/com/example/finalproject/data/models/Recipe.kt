package com.example.finalproject.data.models

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