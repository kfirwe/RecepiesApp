package com.example.finalproject


import android.net.Uri
import com.example.finalproject.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseUtils {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

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
    fun uploadProfilePicture(imageUri: Uri, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)
        val profileRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        profileRef.putFile(imageUri)
            .addOnSuccessListener {
                profileRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = userProfileChangeRequest {
                        photoUri = uri
                    }
                    FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            callback(task.isSuccessful)
                        }
                }
            }
            .addOnFailureListener {
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
                    val id = document.id.toIntOrNull() // Convert to Int or return null
                    id?.let {
                        document.toObject(Recipe::class.java)?.copy(id = it)
                    }
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
                val profilePictureUrl = documentSnapshot.getString("profilePictureUrl") // Optional, if exists

                val userProfile = UserProfile(
                    displayName = name,
                    bio = bio,
                    profilePictureUrl = profilePictureUrl
                )
                callback(userProfile)
            }
            .addOnFailureListener {
                callback(null)
            }
    }


}
