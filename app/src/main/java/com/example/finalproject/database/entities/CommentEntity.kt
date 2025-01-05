package com.example.finalproject.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String, // Firestore Comment ID
    val recipeId: String, // Related Recipe ID
    val userName: String,
    val commentText: String,
    val rating: Int,
    val timestamp: Long
)
