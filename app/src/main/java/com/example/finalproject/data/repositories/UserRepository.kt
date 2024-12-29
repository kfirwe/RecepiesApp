package com.example.finalproject.data.repositories

import android.graphics.Bitmap


import android.graphics.BitmapFactory
import android.util.Base64
import android.content.Context
import com.example.finalproject.R
import com.example.finalproject.data.models.UserProfile
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entities.UserImage
import com.example.finalproject.utils.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProfile(): UserProfile? {
        return FirebaseUtils.getUserProfile()
    }

    suspend fun updateProfileName(newName: String): Boolean {
        return FirebaseUtils.updateDisplayName(newName)
    }

    suspend fun saveUser(userId: String, name: String, email: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        try {
            firestore.collection("users").document(userId).set(user).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadDefaultProfilePicture(userId: String, context: Context) {
        val defaultImage = BitmapFactory.decodeResource(context.resources, R.drawable.default_user_profile)
        val outputStream = ByteArrayOutputStream()
        defaultImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()

        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        try {
            firestore.collection("users").document(userId).update("profilePictureBase64", base64Image).await()
        } catch (e: Exception) {
            throw e
        }
    }
}
