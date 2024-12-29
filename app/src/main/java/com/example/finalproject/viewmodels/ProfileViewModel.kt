package com.example.finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.models.UserProfile
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.data.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

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
        viewModelScope.launch {
            try {
                userRepository.updateProfileName(newName)
                fetchUserProfile() // Refresh profile data
            } catch (e: Exception) {
                _errorMessage.value = e.message
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
