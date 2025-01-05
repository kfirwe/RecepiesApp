package com.example.finalproject.data.models

import com.example.finalproject.database.entities.RecipeEntity

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

    // Convert a RecipeEntity to a Recipe
    companion object {
        fun fromEntity(entity: RecipeEntity): Recipe {
            return Recipe(
                id = entity.id,
                userId = entity.userId,
                title = entity.title,
                description = entity.description,
                imageBase64 = null // No image stored in Room
            )
        }
    }


    // Convert a Recipe to a RecipeEntity
    fun toEntity(): RecipeEntity {
        return RecipeEntity(
            id = this.id,
            userId = this.userId,
            title = this.title,
            description = this.description ?: "" // Default description to empty string
        )
    }
}
