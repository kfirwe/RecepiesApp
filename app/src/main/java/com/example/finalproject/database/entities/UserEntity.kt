package com.example.finalproject.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String, // Firebase UID
    val email: String,
    val password: String,
    val name: String? = null // Optional, in case you want to save it locally
)
