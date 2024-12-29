package com.example.finalproject.data.repositories


import com.example.finalproject.data.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipesUserHomeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun fetchUserRecipes(limit: Long, lastVisible: String?): List<Recipe> {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        var query = firestore.collection("recipes")
            .whereEqualTo("userId", userId)
            .limit(limit)

        lastVisible?.let {
            val lastDocument = firestore.collection("recipes").document(it).get().await()
            query = query.startAfter(lastDocument)
        }

        return try {
            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { it.toObject(Recipe::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            throw e
        }
    }
}
