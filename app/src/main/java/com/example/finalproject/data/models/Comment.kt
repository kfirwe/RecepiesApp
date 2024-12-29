package com.example.finalproject.data.models

import com.google.firebase.Timestamp

data class Comment(
    val userName: String = "",
    val userId: String? = null,
    val commentText: String = "",
    val rating: Int = 0,
    val timestamp: Timestamp = Timestamp.now(), // Firestore Timestamp
    val id: String = "" // Unique comment ID for updates/deletes
)