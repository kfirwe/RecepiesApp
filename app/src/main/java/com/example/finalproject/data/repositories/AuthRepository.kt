package com.example.finalproject.data.repositories


import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun signUp(email: String, password: String): String {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid ?: throw Exception("Failed to get user ID")
        } catch (e: Exception) {
            throw e
        }
    }
}
