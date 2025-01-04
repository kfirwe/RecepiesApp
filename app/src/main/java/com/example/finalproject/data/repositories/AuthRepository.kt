package com.example.finalproject.data.repositories


import android.content.Context
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(context).userDao()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return false

            // Save credentials to Room
            withContext(Dispatchers.IO) {
                userDao.clearUsers() // Clear previous credentials
                userDao.insertUser(UserEntity(userId, email, password))
            }
            true
        } catch (e: Exception) {
//            throw e
            // Attempt offline login
            withContext(Dispatchers.IO) {
                val localUser = userDao.getUserByEmail(email)
                localUser != null && localUser.password == password
            }
        }
    }

    suspend fun getLoggedInUser(): UserEntity? {
        return withContext(Dispatchers.IO) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            currentUserId?.let { userDao.getUserById(it) }
        }
    }

    suspend fun clearSavedUser() {
        withContext(Dispatchers.IO) {
            userDao.clearUsers()
        }
    }

    suspend fun updateUserPassword(userId: String, newPassword: String) {
        withContext(Dispatchers.IO) {
            val existingUser = userDao.getUserById(userId)
            if (existingUser != null) {
                val updatedUser = existingUser.copy(password = newPassword)
                userDao.insertUser(updatedUser)
            }
        }
    }





//    suspend fun login(email: String, password: String): Boolean {
//        return try {
//            auth.signInWithEmailAndPassword(email, password).await()
//            true
//        } catch (e: Exception) {
//            throw e
//        }
//    }


    suspend fun signUp(email: String, password: String): String {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid ?: throw Exception("Failed to get user ID")
        } catch (e: Exception) {
            throw e
        }
    }
}
