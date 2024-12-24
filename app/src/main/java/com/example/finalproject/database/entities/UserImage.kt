package com.example.finalproject.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "user_images")
data class UserImage(
    @PrimaryKey val userKey: String, // Firestore user key
    @ColumnInfo(name = "image_path") val imagePath: String // Path to the image stored locally
)
