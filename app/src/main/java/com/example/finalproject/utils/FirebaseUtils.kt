package com.example.finalproject.utils


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object FirebaseUtils {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Fetch Current User
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Update Display Name
    suspend fun updateDisplayName(newName: String): Boolean {
        val user = auth.currentUser ?: return false
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }
        return try {
            user.updateProfile(profileUpdates).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Update Bio
    suspend fun updateBio(newBio: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            firestore.collection("users").document(userId)
                .update("bio", newBio)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Update Password
    suspend fun updatePassword(newPassword: String): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            user.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Upload Profile Picture
    suspend fun uploadProfilePicture(imageUri: Uri): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            val inputStream = auth.app.applicationContext.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            firestore.collection("users").document(userId)
                .update("profilePictureBase64", base64Image)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Fetch Uploaded Recipes
    suspend fun getUploadedRecipes(): List<Recipe> {
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

    // Add Recipe to Firestore
    suspend fun addRecipe(recipe: Recipe): Boolean {
        return try {
            firestore.collection("recipes").add(recipe).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete Recipe
    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            firestore.collection("recipes").document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Update Recipe
    suspend fun updateRecipe(recipeId: String, updatedData: Map<String, Any>): Boolean {
        return try {
            firestore.collection("recipes").document(recipeId).update(updatedData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Get User Profile
    suspend fun getUserProfile(): UserProfile? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val name = snapshot.getString("name")
            val bio = snapshot.getString("bio")
            val profilePictureBase64 = snapshot.getString("profilePictureBase64")
            UserProfile(
                displayName = name,
                bio = bio,
                profilePictureBase64 = profilePictureBase64
            )
        } catch (e: Exception) {
            null
        }
    }
}
