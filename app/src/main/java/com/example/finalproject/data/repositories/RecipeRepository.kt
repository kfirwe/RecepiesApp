package com.example.finalproject.data.repositories


import com.example.finalproject.data.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecipeRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var lastDocumentSnapshot: QuerySnapshot? = null

    suspend fun fetchAllRecipes(pageSize: Int): List<Recipe> {
        return try {
            val query = firestore.collection("recipes")
                .limit(pageSize.toLong())

            val paginatedQuery = lastDocumentSnapshot?.let { query.startAfter(it.documents.last()) } ?: query

            val snapshot = paginatedQuery.get().await()

            if (snapshot.documents.isNotEmpty()) {
                lastDocumentSnapshot = snapshot
            }

            // Log fetched documents
            snapshot.documents.forEach { document ->
                println("Fetched Recipe: ${document.id} => ${document.data}")
            }

            val recipes = snapshot.documents.mapNotNull { it.toObject(Recipe::class.java)?.copy(id = it.id) }
            recipes.shuffled() // Shuffle the list of recipes
        } catch (e: Exception) {
            println("Error fetching recipes: ${e.message}")
            emptyList()
        }
    }


    suspend fun fetchUserRecipes(): List<Recipe> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = firestore.collection("recipes")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Recipe::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateRecipe(recipeId: String, updatedData: Map<String, Any>) {
        try {
            firestore.collection("recipes").document(recipeId).update(updatedData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addRecipe(title: String, description: String, imageBase64: String) {
        val recipeId = UUID.randomUUID().toString()
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        val recipeData = hashMapOf(
            "id" to recipeId,
            "title" to title,
            "description" to description,
            "imageBase64" to imageBase64,
            "userId" to userId
        )

        firestore.collection("recipes").document(recipeId).set(recipeData).await()
    }

    suspend fun deleteRecipe(recipeId: String) {
        try {
            firestore.collection("recipes").document(recipeId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun searchRecipes(
        queryText: String,
        searchBy: String,
        condition: String,
        limit: Int
    ): List<Recipe> {
        return try {
            var firestoreQuery: Query = firestore.collection("recipes")

            firestoreQuery = when (condition) {
                "Starts With" -> firestoreQuery.whereGreaterThanOrEqualTo(searchBy, queryText)
                    .whereLessThan(searchBy, queryText + '\uf8ff')
                "Exact Match" -> firestoreQuery.whereEqualTo(searchBy, queryText)
                else -> firestoreQuery
            }

            val snapshot = firestoreQuery.limit(limit.toLong()).get().await()
            snapshot.documents.mapNotNull { it.toObject(Recipe::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
