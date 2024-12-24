package com.example.finalproject


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.finalproject.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.util.Base64
import java.io.ByteArrayOutputStream

object FirebaseUtils {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Fetch Current User
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Update Display Name
    fun updateDisplayName(newName: String, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            it.updateProfile(profileUpdates).addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        } ?: callback(false)
    }

    // Update Bio
    fun updateBio(newBio: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        val bioMap = mapOf("bio" to newBio)

        firestore.collection("users").document(userId)
            .set(bioMap, SetOptions.merge())
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Update Password
    fun updatePassword(newPassword: String, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            callback(task.isSuccessful)
        } ?: callback(false)
    }

    // Upload Profile Picture
    // Upload Profile Picture as PNG (Base64) to Firestore
    fun uploadProfilePicture(imageUri: Uri, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)

        try {
            // Open input stream from the provided URI
            val inputStream = FirebaseAuth.getInstance().app.applicationContext.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Convert Bitmap to Base64 (ensure it's PNG)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Ensure PNG format
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Save Base64 string to Firestore
            val firestore = FirebaseFirestore.getInstance()
            val userDoc = firestore.collection("users").document(userId)
            val profileUpdates = mapOf("profilePictureBase64" to base64Image)

            userDoc.update(profileUpdates)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)
        }
    }



    // Fetch Uploaded Recipes
    fun getUploadedRecipes(callback: (List<Recipe>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(emptyList())

        firestore.collection("recipes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Recipe::class.java)?.copy(id = document.id) // Use document ID as the recipe ID
                }

                callback(recipes)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }



    // Add Recipe to Firestore
    fun addRecipe(recipe: Recipe, callback: (Boolean) -> Unit) {
        firestore.collection("recipes").add(recipe)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Delete Recipe
    fun deleteRecipe(recipeId: String, callback: (Boolean) -> Unit) {
        firestore.collection("recipes").document(recipeId)
            .delete()
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Update Recipe
    fun updateRecipe(recipeId: String, updatedRecipe: Recipe, callback: (Boolean) -> Unit) {
        firestore.collection("recipes").document(recipeId)
            .set(updatedRecipe)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun getUserProfile(callback: (UserProfile?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(null)

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name")
                val bio = documentSnapshot.getString("bio")
                val email = documentSnapshot.getString("email")
                val createdAt = documentSnapshot.getLong("createdAt")
                val profilePictureBase64 = documentSnapshot.getString("profilePictureBase64") // Optional, if exists

                val userProfile = UserProfile(
                    displayName = name,
                    bio = bio,
                    profilePictureBase64 = profilePictureBase64
                )
                callback(userProfile)
            }
            .addOnFailureListener {
                callback(null)
            }
    }


}
