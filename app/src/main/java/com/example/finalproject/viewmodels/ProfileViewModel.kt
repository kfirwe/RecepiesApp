package com.example.finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.models.UserProfile
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _passwordChangeStatus = MutableLiveData<Boolean>()
    val passwordChangeStatus: LiveData<Boolean> get() = _passwordChangeStatus

    private val recipeRepository = RecipeRepository()
    private val userRepository = UserRepository()

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

    fun updatePassword(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _passwordChangeStatus.value = true
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
                fetchUserProfile() // Refresh the profile data
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
                userRepository.updateUserBio(newBio) // Call the repository method to update the bio
                fetchUserProfile() // Refresh the profile data
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
                userRepository.updateProfileName(newName) // Calls the repository
                fetchUserProfile() // Refresh the profile data
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
