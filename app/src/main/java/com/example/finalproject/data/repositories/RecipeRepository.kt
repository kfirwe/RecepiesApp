package com.example.finalproject.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.database.dao.RecipeDao
import com.example.finalproject.database.entities.RecipeEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecipeRepository(private val recipeDao: RecipeDao) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var lastDocumentSnapshot: QuerySnapshot? = null

//    fun getRecipesFromRoom(): LiveData<List<RecipeEntity>> {
//        return recipeDao.getLatestRecipes()
//    }
//
//    suspend fun fetchAndStoreRecipes(pageSize: Int) {
//        val recipes = fetchAllRecipes(pageSize) // Fetch from Firestore
//        recipeDao.insertRecipes(recipes.map { recipe ->
//            RecipeEntity(
//                id = recipe.id,
//                userId = recipe.userId,
//                title = recipe.title,
//                description = recipe.description ?: ""
//            )
//        })
//    }
//
//
//    suspend fun fetchAllRecipes(pageSize: Int): List<Recipe> {
//        return try {
//            val query = firestore.collection("recipes").limit(pageSize.toLong())
//            val paginatedQuery = lastDocumentSnapshot?.let { query.startAfter(it.documents.last()) } ?: query
//            val snapshot = paginatedQuery.get().await()
//
//            if (snapshot.documents.isNotEmpty()) {
//                lastDocumentSnapshot = snapshot
//            }
//
//            snapshot.documents.mapNotNull { document ->
//                document.toObject(Recipe::class.java)?.copy(id = document.id)
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

    // Get recipes from Room as `RecipeEntity`
    fun getRecipesFromRoom(): LiveData<List<RecipeEntity>> {
        return recipeDao.getLatestRecipes()
    }

    // Fetch from Firestore and store in Room without images
    suspend fun fetchAndStoreRecipes(pageSize: Int): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val recipes = fetchAllRecipes(pageSize)

            // Store only metadata (no images) in Room
            recipeDao.insertRecipes(recipes.map { recipe ->
                RecipeEntity(
                    id = recipe.id,
                    userId = recipe.userId,
                    title = recipe.title,
                    description = recipe.description ?: ""
                )
            })

            recipes // Return the full Recipe objects (with images) for UI
        } catch (e: Exception) {
            println("Error fetching from Firestore: ${e.message}")
            emptyList() // Return empty list if fetch fails
        }
    }

    // Fetch all recipes from Firestore
    private suspend fun fetchAllRecipes(pageSize: Int): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val query = firestore.collection("recipes")
                .limit(pageSize.toLong())

            val paginatedQuery = lastDocumentSnapshot?.let { query.startAfter(it.documents.last()) } ?: query
            val snapshot = paginatedQuery.get().await()

            if (snapshot.documents.isNotEmpty()) {
                lastDocumentSnapshot = snapshot
            }

            snapshot.documents.mapNotNull { document ->
                document.toObject(Recipe::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            println("Error fetching recipes: ${e.message}")
            emptyList()
        }
    }


//    suspend fun fetchAllRecipes(pageSize: Int): List<RecipeEntity> {
//        return withContext(Dispatchers.IO) { // Ensure the operation runs on the IO thread
//            try {
//                // Build the query with pagination
//                val query = firestore.collection("recipes")
//                    .limit(pageSize.toLong())
//
//                val paginatedQuery = lastDocumentSnapshot?.let { query.startAfter(it.documents.last()) } ?: query
//
//                // Fetch data from Firestore
//                val snapshot = paginatedQuery.get().await()
//
//                if (snapshot.documents.isNotEmpty()) {
//                    lastDocumentSnapshot = snapshot // Update the last snapshot for pagination
//                }
//
//                // Map Firestore documents to RecipeEntity objects
//                val recipeEntities = snapshot.documents.mapNotNull { document ->
//                    val recipe = document.toObject(Recipe::class.java)?.copy(id = document.id)
//                    println("Fetched Recipe: ${document.id} => ${document.data}") // Debug log
//                    recipe?.let {
//                        RecipeEntity(
//                            id = it.id,
//                            userId = it.userId,
//                            title = it.title,
//                            description = it.description ?: "" // Default empty description
//                        )
//                    }
//                }
//
//                // Save fetched recipes to Room
//                recipeDao.clearRecipes() // Clear old recipes to prevent duplication
//                recipeDao.insertRecipes(recipeEntities) // Insert into Room
//                println("Inserted ${recipeEntities.size} recipes into Room")
//
//                // Return the Room entities
//                recipeEntities
//            } catch (e: Exception) {
//                println("Error fetching recipes from Firestore: ${e.message}")
//
//                // Fallback to Room in case of failure
//                val cachedRecipes = recipeDao.getLatestRecipes()
//                println("Fetched ${cachedRecipes.size} recipes from Room as fallback")
//                cachedRecipes
//            }
//        }
//    }





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
