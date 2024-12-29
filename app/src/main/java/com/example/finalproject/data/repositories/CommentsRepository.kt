package com.example.finalproject.data.repositories


import com.example.finalproject.data.models.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class CommentsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchComments(recipeId: String): List<Comment> {
        return try {
            val snapshot = firestore.collection("recipes").document(recipeId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Comment::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addComment(recipeId: String, comment: Comment) {
        try {
            firestore.collection("recipes").document(recipeId).collection("comments")
                .add(comment)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateComment(recipeId: String, commentId: String, updatedData: Map<String, Any>) {
        try {
            firestore.collection("recipes").document(recipeId)
                .collection("comments")
                .document(commentId)
                .update(updatedData)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteComment(recipeId: String, commentId: String) {
        try {
            firestore.collection("recipes").document(recipeId)
                .collection("comments")
                .document(commentId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}
