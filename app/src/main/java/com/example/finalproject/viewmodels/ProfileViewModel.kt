package com.example.finalproject.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.models.UserProfile
import com.example.finalproject.data.repositories.AuthRepository
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.data.repositories.UserRepository
import com.example.finalproject.database.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val recipeRepository: RecipeRepository
    private val userRepository: UserRepository

    init {
        val database = AppDatabase.getDatabase(context)
        val recipeDao = database.recipeDao()
        val userDao = database.userDao()
        recipeRepository = RecipeRepository(recipeDao)
        userRepository = UserRepository(userDao)
    }

    private val _passwordChangeStatus = MutableLiveData<Boolean>()
    val passwordChangeStatus: LiveData<Boolean> get() = _passwordChangeStatus

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val profile = userRepository.getProfile()
                _userProfile.value = profile
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.clearSavedUser()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to clear saved user: ${e.message}"
            }
        }
    }

    fun updatePassword(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            try {
                                authRepository.updateUserPassword(user.uid, newPassword)
                                _passwordChangeStatus.postValue(true)
                            } catch (e: Exception) {
                                _errorMessage.postValue("Password updated in Firebase but failed locally: ${e.message}")
                            }
                        }
                    } else {
                        _errorMessage.value = task.exception?.message
                    }
                }
        } else {
            _errorMessage.value = "User is not logged in."
        }
    }

    fun updateProfilePicture(base64Image: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.updateProfilePicture(base64Image)
                fetchUserProfile()
                _errorMessage.value = "Profile picture updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile picture: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserBio(newBio: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.updateUserBio(newBio)
                fetchUserProfile()
                _errorMessage.value = "Bio updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update bio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _recipes.value = recipeRepository.fetchUserRecipes()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDisplayName(newName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.updateProfileName(newName)
                fetchUserProfile()
                _errorMessage.value = "Display name updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update display name: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRecipe(recipeId: String, updatedData: Map<String, Any>) {
        viewModelScope.launch {
            try {
                recipeRepository.updateRecipe(recipeId, updatedData)
                fetchUserRecipes()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteRecipe(recipeId)
                fetchUserRecipes()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
